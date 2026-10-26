package com.example.weatherwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;

public class WeatherAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget_layout);

        // Получаем кэшированные или свежие данные
        WeatherRepository.WeatherData data = WeatherRepository.getFromCache(context);
        
        views.setTextViewText(R.id.widget_temp, String.format("%.1f°C", data.temperature));
        views.setTextViewText(R.id.widget_condition, data.condition);

        // Динамическая адаптация темы (день / ночь) в стиле жидкого стекла
        if (data.isDay) {
            views.setInt(R.id.widget_root_layout, "setBackgroundResource", R.drawable.widget_glass_background);
            views.setTextColor(R.id.widget_temp, Color.WHITE);
            views.setTextColor(R.id.widget_city, Color.WHITE);
        } else {
            // Ночной темный оттенок стекла
            views.setInt(R.id.widget_root_layout, "setBackgroundResource", R.drawable.widget_glass_background);
            views.setTextColor(R.id.widget_temp, Color.parseColor("#E0F7FA"));
            views.setTextColor(R.id.widget_city, Color.parseColor("#B2EBF2"));
        }

        // Клик по виджету открывает ForecastActivity
        Intent intent = new Intent(context, ForecastActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_root_layout, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateAllWidgets(Context context) {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        ComponentName widgetComponent = new ComponentName(context, WeatherAppWidgetProvider.class);
        int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
        for (int id : widgetIds) {
            updateAppWidget(context, widgetManager, id);
        }
    }
}
