package com.example.weathermaster;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.IconCompat;

public class ForegroundService extends Service {
    private static final String ACTION_DESTROY_NOTIFICATION = "com.example.weathermaster.ACTION_DESTROY_NOTIFICATION";
    private static final String ACTION_DESTROY_APPEARANCE = "com.example.weathermaster.ACTION_DESTROY_APPEARANCE";

    private static final String CHANNEL_ID = "ForegroundServiceChannel";


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if (ACTION_DESTROY_NOTIFICATION.equals(action)) {
            destroyNotification();
        } else {
            String condition = intent.getStringExtra("condition");
            String locationWeather = intent.getStringExtra("locationWeather");
            String uvindex = intent.getStringExtra("uvindexValue");
            String iconCodeCondition = intent.getStringExtra("ICONCODE");
            String ISDAY = intent.getStringExtra("ISDAY");
            String chanceOfRain = intent.getStringExtra("chanceOfRain");

            updateNotification(condition, locationWeather, uvindex, iconCodeCondition, ISDAY, chanceOfRain);
        }

        return START_STICKY;
    }


    public void destroyNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(1);
        }
        stopSelf();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void updateNotification(String condition, String locationWeather, String uvindex, String iconCodeCondition, String ISDAY, String chanceOfRain) {

        // Set the appropriate icon for the weather condition
        int weathericon = R.drawable.sunny;

        if (ISDAY.equals("1")) {
            if (iconCodeCondition.equals("0")) {
                weathericon = R.drawable.sunny;
            } else if (iconCodeCondition.equals("1")) {

                weathericon = R.drawable.mostly_sunny;

            } else if (iconCodeCondition.equals("2")) {

                weathericon = R.drawable.partly_cloudy;

            } else if (iconCodeCondition.equals("3")) {

                weathericon = R.drawable.cloudy;


            } else if (iconCodeCondition.equals("45") || iconCodeCondition.equals("46")) {

                weathericon = R.drawable.haze_fog_dust_smoke;


            } else if (iconCodeCondition.equals("51") || iconCodeCondition.equals("53") || iconCodeCondition.equals("55")) {
                weathericon = R.drawable.drizzle;

            } else if (iconCodeCondition.equals("56") || iconCodeCondition.equals("57")) {


                weathericon = R.drawable.wintry_mix_rain_snow;


            } else if (iconCodeCondition.equals("61") || iconCodeCondition.equals("63")) {

                weathericon = R.drawable.showers_rain;


            } else if (iconCodeCondition.equals("65")) {
                weathericon = R.drawable.heavy_rain;


            } else if (iconCodeCondition.equals("66") || iconCodeCondition.equals("67")) {
                weathericon = R.drawable.sleet_hail;


            } else if (iconCodeCondition.equals("71")) {
                weathericon = R.drawable.snow_showers_snow;

            } else if (iconCodeCondition.equals("73")) {

                weathericon = R.drawable.snow_showers_snow;


            } else if (iconCodeCondition.equals("75")) {

                weathericon = R.drawable.heavy_snow;

            } else if (iconCodeCondition.equals("77")) {
                weathericon = R.drawable.flurries;

            } else if (iconCodeCondition.equals("80") || iconCodeCondition.equals("81")) {

                weathericon = R.drawable.showers_rain;


            } else if (iconCodeCondition.equals("82")) {


                weathericon = R.drawable.heavy_rain;


            } else if (iconCodeCondition.equals("85")) {

                weathericon = R.drawable.snow_showers_snow;


            } else if (iconCodeCondition.equals("86")) {

                weathericon = R.drawable.heavy_snow;


            } else if (iconCodeCondition.equals("95")) {


                weathericon = R.drawable.isolated_scattered_tstorms_day;


            } else if (iconCodeCondition.equals("96") || iconCodeCondition.equals("99")) {

                weathericon = R.drawable.strong_tstorms;


            }
        } else {
            if (iconCodeCondition.equals("0")) {

                weathericon = R.drawable.clear_night;

            } else if (iconCodeCondition.equals("1")) {

                weathericon = R.drawable.mostly_clear_night;

            } else if (iconCodeCondition.equals("2")) {

                weathericon = R.drawable.partly_cloudy_night;

            } else if (iconCodeCondition.equals("3")) {

                weathericon = R.drawable.cloudy;


            } else if (iconCodeCondition.equals("45") || iconCodeCondition.equals("46")) {

                weathericon = R.drawable.haze_fog_dust_smoke;


            } else if (iconCodeCondition.equals("51") || iconCodeCondition.equals("53") || iconCodeCondition.equals("55")) {

                weathericon = R.drawable.drizzle;

            } else if (iconCodeCondition.equals("56") || iconCodeCondition.equals("57")) {


                weathericon = R.drawable.wintry_mix_rain_snow;


            } else if (iconCodeCondition.equals("61") || iconCodeCondition.equals("63")) {

                weathericon = R.drawable.showers_rain;


            } else if (iconCodeCondition.equals("65")) {
                weathericon = R.drawable.heavy_rain;


            } else if (iconCodeCondition.equals("66") || iconCodeCondition.equals("67")) {
                weathericon = R.drawable.sleet_hail;


            } else if (iconCodeCondition.equals("71")) {
                weathericon = R.drawable.snow_showers_snow;

            } else if (iconCodeCondition.equals("73")) {

                weathericon = R.drawable.snow_showers_snow;


            } else if (iconCodeCondition.equals("75")) {

                weathericon = R.drawable.heavy_snow;

            } else if (iconCodeCondition.equals("77")) {
                weathericon = R.drawable.flurries;

            } else if (iconCodeCondition.equals("80") || iconCodeCondition.equals("81")) {

                weathericon = R.drawable.showers_rain;


            } else if (iconCodeCondition.equals("82")) {


                weathericon = R.drawable.heavy_rain;


            } else if (iconCodeCondition.equals("85")) {

                weathericon = R.drawable.snow_showers_snow;


            } else if (iconCodeCondition.equals("86")) {

                weathericon = R.drawable.heavy_snow;


            } else if (iconCodeCondition.equals("95")) {


                weathericon = R.drawable.isolated_scattered_tstorms_night;


            } else if (iconCodeCondition.equals("96") || iconCodeCondition.equals("99")) {

                weathericon = R.drawable.strong_tstorms;


            }

        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.baseline_cloud_queue_24)
                    .setContentTitle(condition)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(uvindex + "\nPrecipitation chances: " + chanceOfRain))
                    .setSubText(locationWeather)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), weathericon))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(false)
                    .build();
        }
        notification.flags |= Notification.FLAG_NO_CLEAR;

        startForeground(1, notification);
    }


    public Bitmap createTempIcon(String temperatureText) {
        int width = 150;
        int height = 150;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            paint.setColor(Color.WHITE);
        } else {
            paint.setColor(Color.BLACK);
        }

        paint.setTextSize(110);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));

        float xPos = canvas.getWidth() / 2;
        float yPos = (canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2);
        canvas.drawText(temperatureText, xPos, yPos, paint);

        return bitmap;
    }


    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }


    public void UpdateNotification(String condition, String locationWeather, String uvindex, String iconCodeCondition, String ISDAY, String chanceOfRain) {
        updateNotification(condition, locationWeather, uvindex, iconCodeCondition, ISDAY, chanceOfRain);
    }

}
