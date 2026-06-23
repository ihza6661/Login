package com.ryan.login;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherWidgetProvider extends AppWidgetProvider {

    private static final String API_KEY = "2fc1bfc247e80a7678b8b6b0f2e00aaa";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Membaca data terakhir yang disimpan dari CuacaActivity
        SharedPreferences prefs = context.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE);
        String lat = prefs.getString("LAST_LAT", null);
        String lon = prefs.getString("LAST_LON", null);
        String cityName = prefs.getString("LAST_CITY", "Lokasi Belum Diatur");

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_cuaca);
        views.setTextViewText(R.id.widgetCity, cityName.toUpperCase());

        // Aksi klik Widget untuk membuka CuacaActivity
        Intent intent = new Intent(context, CuacaActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widgetTemp, pendingIntent);

        if (lat != null && lon != null) {
            fetchWeatherData(context, appWidgetManager, appWidgetId, views, lat, lon);
        } else {
            views.setTextViewText(R.id.widgetDesc, "Buka aplikasi sekali");
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private static void fetchWeatherData(Context context, AppWidgetManager appWidgetManager, int appWidgetId, RemoteViews views, String lat, String lon) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                String urlString = "https://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY;
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray list = jsonResponse.getJSONArray("list");
                JSONObject currentItem = list.getJSONObject(0);

                JSONObject mainObj = currentItem.getJSONObject("main");
                double temp = mainObj.getDouble("temp") - 273.15;
                double tempMin = mainObj.getDouble("temp_min") - 273.15;
                double tempMax = mainObj.getDouble("temp_max") - 273.15;

                JSONObject weatherObj = currentItem.getJSONArray("weather").getJSONObject(0);
                String desc = weatherObj.getString("description");
                String iconCode = weatherObj.getString("icon");

                String finalTemp = String.format(Locale.US, "%.0f°", temp);
                String finalHighLow = String.format(Locale.US, "%.0f° / %.0f°", tempMax, tempMin);
                String iconUrl = "http://openweathermap.org/img/wn/" + iconCode + "@2x.png";

                views.setTextViewText(R.id.widgetTemp, finalTemp);
                views.setTextViewText(R.id.widgetDesc, desc.substring(0, 1).toUpperCase() + desc.substring(1));
                views.setTextViewText(R.id.widgetHighLow, finalHighLow);

                AppWidgetTarget appWidgetTarget = new AppWidgetTarget(context, R.id.widgetIcon, views, appWidgetId);
                Glide.with(context.getApplicationContext())
                        .asBitmap()
                        .load(iconUrl)
                        .into(appWidgetTarget);

                appWidgetManager.updateAppWidget(appWidgetId, views);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}