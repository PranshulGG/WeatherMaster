package com.example.weathermaster;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class WidgetProviderSquareV2 extends AppWidgetProvider {

    private static final String PREFS_NAME = "WeatherWidgetPrefsSquareV2";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Retrieve persisted data
        String condition = prefs.getString("condition", "--");
        String locationWeather = prefs.getString("locationWeather", "--");
        String mainTemp = prefs.getString("mainTemp", "--°");
        String iconData = prefs.getString("iconData", "cloudy");
        String highLow = prefs.getString("highLow", "--");

        String hour_0_temp = prefs.getString("hour_0_temp", "--");
        String hour_0_icon = prefs.getString("hour_0_icon", "cloudy");
        String hour_0_time = prefs.getString("hour_0_time", "--");

        String hour_1_temp = prefs.getString("hour_1_temp", "--");
        String hour_1_icon = prefs.getString("hour_1_icon", "cloudy");
        String hour_1_time = prefs.getString("hour_1_time", "--");

        String hour_2_temp = prefs.getString("hour_2_temp", "--");
        String hour_2_icon = prefs.getString("hour_2_icon", "cloudy");
        String hour_2_time = prefs.getString("hour_2_time", "--");

        String hour_3_temp = prefs.getString("hour_3_temp", "--");
        String hour_3_icon = prefs.getString("hour_3_icon", "cloudy");
        String hour_3_time = prefs.getString("hour_3_time", "--");

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_weather_square_v2);

            // Set widget data
            int iconResId = context.getResources().getIdentifier(iconData, "drawable", context.getPackageName());
            int iconResIdHour0 = context.getResources().getIdentifier(hour_0_icon, "drawable", context.getPackageName());
            int iconResIdHour1 = context.getResources().getIdentifier(hour_1_icon, "drawable", context.getPackageName());
            int iconResIdHour2 = context.getResources().getIdentifier(hour_2_icon, "drawable", context.getPackageName());
            int iconResIdHour3 = context.getResources().getIdentifier(hour_3_icon, "drawable", context.getPackageName());

            views.setTextViewText(R.id.weather_condition_square_v2, condition);
            views.setImageViewResource(R.id.weather_icon_square_v2, iconResId);
            views.setTextViewText(R.id.temp_text_square_v2, mainTemp);
            views.setTextViewText(R.id.high_temp_text_square_v2, highLow);

            views.setTextViewText(R.id.hour_0_temp_square_v2, hour_0_temp);
            views.setImageViewResource(R.id.weather_icon_hour0_square_v2, iconResIdHour0);
            views.setTextViewText(R.id.hour_0_time_square_v2, hour_0_time);

            views.setTextViewText(R.id.hour_1_temp_square_v2, hour_1_temp);
            views.setImageViewResource(R.id.weather_icon_hour1_square_v2, iconResIdHour1);
            views.setTextViewText(R.id.hour_1_time_square_v2, hour_1_time);

            views.setTextViewText(R.id.hour_2_temp_square_v2, hour_2_temp);
            views.setImageViewResource(R.id.weather_icon_hour2_square_v2, iconResIdHour2);
            views.setTextViewText(R.id.hour_2_time_square_v2, hour_2_time);

            views.setTextViewText(R.id.hour_3_temp_square_v2, hour_3_temp);
            views.setImageViewResource(R.id.weather_icon_hour3_square_v2, iconResIdHour3);
            views.setTextViewText(R.id.hour_3_time_square_v2, hour_3_time);

            // Add click listener to open MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_layout_square_v2, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    public static void updateWeatherWidgetSquareV2(Context context, String condition, String locationWeather, String mainTemp, String iconData, String highLow,
                                           String hour_0_temp, String hour_0_icon, String hour_0_time,
                                           String hour_1_temp, String hour_1_icon, String hour_1_time,
                                           String hour_2_temp, String hour_2_icon, String hour_2_time,
                                           String hour_3_temp, String hour_3_icon, String hour_3_time) {

        // Save data to SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("condition", condition);
        editor.putString("locationWeather", locationWeather);
        editor.putString("mainTemp", mainTemp);
        editor.putString("iconData", iconData);
        editor.putString("highLow", highLow);

        editor.putString("hour_0_temp", hour_0_temp);
        editor.putString("hour_0_icon", hour_0_icon);
        editor.putString("hour_0_time", hour_0_time);

        editor.putString("hour_1_temp", hour_1_temp);
        editor.putString("hour_1_icon", hour_1_icon);
        editor.putString("hour_1_time", hour_1_time);

        editor.putString("hour_2_temp", hour_2_temp);
        editor.putString("hour_2_icon", hour_2_icon);
        editor.putString("hour_2_time", hour_2_time);

        editor.putString("hour_3_temp", hour_3_temp);
        editor.putString("hour_3_icon", hour_3_icon);
        editor.putString("hour_3_time", hour_3_time);
        editor.apply();

        // Update widget views
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName widgetComponent = new ComponentName(context, WidgetProviderSquareV2.class);
        int[] widgetIds = appWidgetManager.getAppWidgetIds(widgetComponent);

        for (int widgetId : widgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_weather_square_v2);

            int iconResId = context.getResources().getIdentifier(iconData, "drawable", context.getPackageName());
            int iconResIdHour0 = context.getResources().getIdentifier(hour_0_icon, "drawable", context.getPackageName());
            int iconResIdHour1 = context.getResources().getIdentifier(hour_1_icon, "drawable", context.getPackageName());
            int iconResIdHour2 = context.getResources().getIdentifier(hour_2_icon, "drawable", context.getPackageName());
            int iconResIdHour3 = context.getResources().getIdentifier(hour_3_icon, "drawable", context.getPackageName());

            views.setTextViewText(R.id.weather_condition_square_v2, condition);
            views.setImageViewResource(R.id.weather_icon_square_v2, iconResId);
            views.setTextViewText(R.id.temp_text_square_v2, mainTemp);
            views.setTextViewText(R.id.high_temp_text_square_v2, highLow);

            views.setTextViewText(R.id.hour_0_temp_square_v2, hour_0_temp);
            views.setImageViewResource(R.id.weather_icon_hour0_square_v2, iconResIdHour0);
            views.setTextViewText(R.id.hour_0_time_square_v2, hour_0_time);

            views.setTextViewText(R.id.hour_1_temp_square_v2, hour_1_temp);
            views.setImageViewResource(R.id.weather_icon_hour1_square_v2, iconResIdHour1);
            views.setTextViewText(R.id.hour_1_time_square_v2, hour_1_time);

            views.setTextViewText(R.id.hour_2_temp_square_v2, hour_2_temp);
            views.setImageViewResource(R.id.weather_icon_hour2_square_v2, iconResIdHour2);
            views.setTextViewText(R.id.hour_2_time_square_v2, hour_2_time);

            views.setTextViewText(R.id.hour_3_temp_square_v2, hour_3_temp);
            views.setImageViewResource(R.id.weather_icon_hour3_square_v2, iconResIdHour3);
            views.setTextViewText(R.id.hour_3_time_square_v2, hour_3_time);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_layout_square_v2, pendingIntent);

            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }
}
