package com.example.weathermaster;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class WidgetProviderSquare extends AppWidgetProvider {
    private static final String PREFS_NAME = "WeatherWidgetPrefsSquare";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Retrieve persisted data
        String condition = prefs.getString("condition", "--");
        String locationWeather = prefs.getString("locationWeather", "--");
        String mainTemp = prefs.getString("mainTemp", "--°");
        String iconData = prefs.getString("iconData", "cloudy");
        String highLow = prefs.getString("highLow", "--");

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widgets_weather_square);

            // Set widget data
            int iconResId = context.getResources().getIdentifier(iconData, "drawable", context.getPackageName());

            views.setTextViewText(R.id.weather_condition_square, condition);
            views.setImageViewResource(R.id.weather_icon_square, iconResId);
            views.setTextViewText(R.id.temp_text_square, mainTemp);
            views.setTextViewText(R.id.high_temp_text_square, highLow);

            // Add click listener to open MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_layout_square, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    public static void updateWeatherWidgetSquare(Context context, String condition, String locationWeather, String mainTemp, String iconData, String highLow,
                                           String hour_0_temp, String hour_0_icon, String hour_0_time,
                                           String hour_1_temp, String hour_1_icon, String hour_1_time,
                                           String hour_2_temp, String hour_2_icon, String hour_2_time,
                                           String hour_3_temp, String hour_3_icon, String hour_3_time) {

        // Save data to SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("condition", condition);
        editor.putString("mainTemp", mainTemp);
        editor.putString("iconData", iconData);
        editor.putString("highLow", highLow);

        editor.apply();

        // Update widget views
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName widgetComponent = new ComponentName(context, WidgetProviderSquare.class);
        int[] widgetIds = appWidgetManager.getAppWidgetIds(widgetComponent);

        for (int widgetId : widgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widgets_weather_square);

            int iconResId = context.getResources().getIdentifier(iconData, "drawable", context.getPackageName());

            views.setTextViewText(R.id.weather_condition_square, condition);
            views.setImageViewResource(R.id.weather_icon_square, iconResId);
            views.setTextViewText(R.id.temp_text_square, mainTemp);
            views.setTextViewText(R.id.high_temp_text_square, highLow);

            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }
}
