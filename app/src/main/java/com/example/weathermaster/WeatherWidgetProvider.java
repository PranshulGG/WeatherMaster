package com.example.weathermaster;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
public class WeatherWidgetProvider extends AppWidgetProvider {

    private static final String PREFS_NAME = "WeatherWidgetPrefs";


    public static final String ACTION_AUTO_UPDATE = "com.example.weathermaster.ACTION_AUTO_UPDATE";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        // No need for alarm setup, as we will handle date changes via broadcasts
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        // No alarm to cancel, as we don't use it anymore
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Retrieve persisted data

        String mainTemp = prefs.getString("mainTemp", "--°");
        String iconData = prefs.getString("iconData", "cloudy");

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_weather);

            // Set widget data
            int iconResId = context.getResources().getIdentifier(iconData, "drawable", context.getPackageName());


            views.setTextViewText(R.id.glance_temp, mainTemp);
            views.setImageViewResource(R.id.glance_icon, iconResId);
            views.setTextViewText(R.id.week_time, currentDate);

            // Add click listener to open MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    public static void updateWeatherWidget(Context context, String condition, String locationWeather, String mainTemp, String iconData, String highLow,
                                           String hour_0_temp, String hour_0_icon, String hour_0_time,
                                           String hour_1_temp, String hour_1_icon, String hour_1_time,
                                           String hour_2_temp, String hour_2_icon, String hour_2_time,
                                           String hour_3_temp, String hour_3_icon, String hour_3_time) {

        // Save data to SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("mainTemp", mainTemp);
        editor.putString("iconData", iconData);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        editor.apply();

        // Update widget views
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName widgetComponent = new ComponentName(context, WeatherWidgetProvider.class);
        int[] widgetIds = appWidgetManager.getAppWidgetIds(widgetComponent);

        for (int widgetId : widgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_weather);

            int iconResId = context.getResources().getIdentifier(iconData, "drawable", context.getPackageName());


            views.setTextViewText(R.id.glance_temp, mainTemp);
            views.setImageViewResource(R.id.glance_icon, iconResId);

            views.setTextViewText(R.id.week_time, currentDate);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        // Check if the action is related to the date or time
        if (Intent.ACTION_DATE_CHANGED.equals(intent.getAction()) ||
                Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {

            // Update the widget with the current date
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName widgetComponent = new ComponentName(context, WeatherWidgetProvider.class);
            int[] widgetIds = appWidgetManager.getAppWidgetIds(widgetComponent);

            // Call onUpdate to refresh the widget
            onUpdate(context, appWidgetManager, widgetIds);
        }
    }
}

