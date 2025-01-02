package com.example.weathermaster;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class WidgetProviderRound extends AppWidgetProvider {

    private static final String PREFS_NAME = "WeatherWidgetPrefsRound";




    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);



        // Retrieve persisted data
        String mainTemp = prefs.getString("mainTemp", "--°");
        String iconData = prefs.getString("iconData", "cloudy");


        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_weather_round);



            // Set widget data
            int iconResId = context.getResources().getIdentifier(iconData, "drawable", context.getPackageName());


            views.setImageViewResource(R.id.weather_icon_round, iconResId);
            views.setTextViewText(R.id.temp_text_round, mainTemp);


            // Add click listener to open MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_layout_round, pendingIntent);



            appWidgetManager.updateAppWidget(appWidgetId, views);
        }


    }

    public static void updateWeatherWidgetRound(Context context, String mainTemp, String iconData) {

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("mainTemp", mainTemp);
        editor.putString("iconData", iconData);


        editor.apply();

        // Update widget views
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName widgetComponent = new ComponentName(context, WidgetProviderRound.class);
        int[] widgetIds = appWidgetManager.getAppWidgetIds(widgetComponent);

        for (int widgetId : widgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_weather_round);

            int iconResId = context.getResources().getIdentifier(iconData, "drawable", context.getPackageName());


            views.setImageViewResource(R.id.weather_icon_round, iconResId);
            views.setTextViewText(R.id.temp_text_round, mainTemp);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_layout_round, pendingIntent);


            appWidgetManager.updateAppWidget(widgetId, views);
        }


    }


}
