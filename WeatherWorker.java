package com.example.weatherwidget;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.util.concurrent.TimeUnit;

public class WeatherWorker extends Worker {

    public WeatherWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        WeatherRepository.WeatherData data = WeatherRepository.fetchWeatherSync(context);
        if (data != null) {
            WeatherAppWidgetProvider.updateAllWidgets(context);
            return Result.success();
        }
        return Result.retry();
    }

    public static void schedulePeriodicWork(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest weatherWorkRequest = new PeriodicWorkRequest.Builder(
                WeatherWorker.java, 2, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "WeatherSyncWork",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                weatherWorkRequest
        );
    }
}
