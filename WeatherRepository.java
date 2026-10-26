package com.example.weatherwidget;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherRepository {
    private static final String PREF_NAME = "weather_cache";
    private static final String KEY_TEMP = "cached_temp";
    private static final String KEY_CONDITION = "cached_condition";
    private static final String KEY_IS_DAY = "cached_is_day";

    // Пример координат (Москва)
    private static final String API_URL = "https://api.open-meteo.com/v1/forecast?latitude=55.7522&longitude=37.6156&current=temperature_2m,weather_code,is_day&daily=weather_code,temperature_2m_max,temperature_2m_min,time&timezone=auto";

    public static WeatherData fetchWeatherSync(Context context) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                WeatherData data = parseJson(result.toString());
                saveToCache(context, data);
                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getFromCache(context);
    }

    private static WeatherData parseJson(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONObject current = json.getJSONObject("current");
            double temp = current.getDouble("temperature_2m");
            int weatherCode = current.getInt("weather_code");
            int isDay = current.getInt("is_day");

            String condition = getWeatherDescription(weatherCode);
            return new WeatherData(temp, condition, isDay == 1);
        } catch (Exception e) {
            return null;
        }
    }

    private static void saveToCache(Context context, WeatherData data) {
        if (data == null) return;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
            .putFloat(KEY_TEMP, (float) data.temperature)
            .putString(KEY_CONDITION, data.condition)
            .putBoolean(KEY_IS_DAY, data.isDay)
            .apply();
    }

    public static WeatherData getFromCache(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (!prefs.contains(KEY_TEMP)) return new WeatherData(0.0, "Нет данных", true);
        double temp = prefs.getFloat(KEY_TEMP, 0f);
        String condition = prefs.getString(KEY_CONDITION, "Неизвестно");
        boolean isDay = prefs.getBoolean(KEY_IS_DAY, true);
        return new WeatherData(temp, condition, isDay);
    }

    public static String getWeatherDescription(int code) {
        switch (code) {
            case 0: return "Ясно";
            case 1:
            case 2: return "Переменная облачность";
            case 3: return "Пасмурно";
            case 51:
            case 53:
            case 55: return "Морось";
            case 61:
            case 63: return "Дождь";
            case 71:
            case 73: return "Снег";
            case 95: return "Гроза";
            default: return "Облачно";
        }
    }

    public static class WeatherData {
        public double temperature;
        public String condition;
        public boolean isDay;

        public WeatherData(double temperature, String condition, boolean isDay) {
            this.temperature = temperature;
            this.condition = condition;
            this.isDay = isDay;
        }
    }
}
