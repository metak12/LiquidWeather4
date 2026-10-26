package com.example.weatherwidget;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ForecastActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        // Инициализация WorkManager при старте приложения
        WeatherWorker.schedulePeriodicWork(getApplicationContext());

        RecyclerView recyclerView = findViewById(R.id.forecast_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Демо-данные для 7 дней (в продакшне парсится из массива daily в Open-Meteo)
        List<DayForecast> forecastList = new ArrayList<>();
        forecastList.add(new DayForecast("Сегодня", "+22°C", "+14°C", "Ясно"));
        forecastList.add(new DayForecast("Завтра", "+20°C", "+12°C", "Облачно"));
        forecastList.add(new DayForecast("Среда", "+18°C", "+10°C", "Дождь"));
        forecastList.add(new DayForecast("Четверг", "+19°C", "+11°C", "Переменная облачность"));
        forecastList.add(new DayForecast("Пятница", "+21°C", "+13°C", "Ясно"));
        forecastList.add(new DayForecast("Суббота", "+23°C", "+15°C", "Ясно"));
        forecastList.add(new DayForecast("Воскресенье", "+17°C", "+9°C", "Гроза"));

        ForecastAdapter adapter = new ForecastAdapter(forecastList);
        recyclerView.setAdapter(adapter);
    }

    public static class DayForecast {
        public String day;
        public String maxTemp;
        public String minTemp;
        public String condition;

        public DayForecast(String day, String maxTemp, String minTemp, String condition) {
            this.day = day;
            this.maxTemp = maxTemp;
            this.minTemp = minTemp;
            this.condition = condition;
        }
    }
}
