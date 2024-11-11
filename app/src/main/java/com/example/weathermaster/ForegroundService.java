package com.example.weathermaster;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.IBinder;
import android.widget.RemoteViews;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.IconCompat;

public class ForegroundService extends Service {
    private static final String ACTION_DESTROY_NOTIFICATION = "com.example.weathermaster.ACTION_DESTROY_NOTIFICATION";
    private static final String ACTION_DESTROY_APPEARANCE = "com.example.weathermaster.ACTION_DESTROY_APPEARANCE";

    private static final String CHANNEL_ID = "ForegroundServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();  // Ensure the notification channel is created
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if (ACTION_DESTROY_NOTIFICATION.equals(action)) {
            destroyNotification();
        } else {
            String temperature = intent.getStringExtra("temperature");
            String condition = intent.getStringExtra("condition");
            updateNotification(temperature, condition);
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


    public void updateNotification(String temperature, String condition) {

        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_layout);
        notificationLayout.setTextViewText(R.id.temp_text, temperature);
        notificationLayout.setTextViewText(R.id.condition_text, condition);
        notificationLayout.setTextViewText(R.id.app_name, "Weather Master");


        if (condition.equals("Clear sky") || condition.equals("Sunny") || condition.equals("Hot")) {
            notificationLayout.setImageViewResource(R.id.weather_icon, R.drawable.clear_day);
        } else if (condition.equals("Mostly clear") || condition.equals("Partly cloudy") || condition.equals("Mostly Sunny") || condition.equals("Partly Sunny") || condition.equals("Hazy Sunshine")) {
            notificationLayout.setImageViewResource(R.id.weather_icon, R.drawable.partly_cloudy_day);
        } else if (condition.equals("Overcast") || condition.equals("Mostly Cloudy") || condition.equals("Cloudy") || condition.equals("Intermittent Clouds") || condition.equals("Cloudy / snow")) {
            notificationLayout.setImageViewResource(R.id.weather_icon, R.drawable.cloudy);
        } else if (condition.equals("Fog") || condition.equals("Hazy Moonlight")) {
            notificationLayout.setImageViewResource(R.id.weather_icon, R.drawable.haze_fog_dust_smoke);
        } else if (condition.equals("Drizzle") || condition.equals("Freezing Drizzle") || condition.equals("Moderate rain") || condition.equals("Freezing Rain") || condition.equals("Heavy intensity rain") || condition.equals("Rain showers") || condition.equals("Heavy rain showers") || condition.equals("Showers") || condition.equals("Light rain") || condition.equals("Rainy") || condition.equals("Rain") || condition.equals("Moderate Flurries") || condition.equals("Sleet") || condition.equals("Rain and Snow") || condition.equals("Freezing Rain")) {
            notificationLayout.setImageViewResource(R.id.weather_icon, R.drawable.showers_rain);
        } else if (condition.equals("slight_snow") || condition.equals("Moderate snow") || condition.equals("Heavy intensity snow") || condition.equals("Snow grains") || condition.equals("Slight snow showers") || condition.equals("Heavy snow showers") || condition.equals("Flurries") || condition.equals("Snow") || condition.equals("Light flurries") || condition.equals("Light snow")) {
            notificationLayout.setImageViewResource(R.id.weather_icon, R.drawable.showers_snow);
        } else if (condition.equals("Thunderstorm") || condition.equals("Strong thunderstorm") || condition.equals("T-Storms") || condition.equals("Cloudy / T-Storms") || condition.equals("Sunny / T-Storms") || condition.equals("Light T-storms")) {
            notificationLayout.setImageViewResource(R.id.weather_icon, R.drawable.isolated_thunderstorms);
        } else  if (condition.equals("Clear")){
            notificationLayout.setImageViewResource(R.id.weather_icon, R.drawable.clear_night);
        } else  if (condition.equals("Mostly Clear")){
            notificationLayout.setImageViewResource(R.id.weather_icon, R.drawable.partly_cloudy_night);
        } else {
            notificationLayout.setImageViewResource(R.id.weather_icon, R.drawable.haze_fog_dust_smoke);

        }


        // Create a dynamic temperature icon as a Bitmap
        Bitmap tempIconBitmap = createTempIcon(temperature);

        // Build the updated notification
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(IconCompat.createWithBitmap(tempIconBitmap))
                    .setCustomContentView(notificationLayout)
                    .setContentTitle("Weather Master")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setOngoing(true)
                    .build();
        }


        startForeground(1, notification);
    }


    public Bitmap createTempIcon(String temperatureText) {
        int width = 150;
        int height = 150;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
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


    public void UpdateNotification(String temperature, String condition) {
        updateNotification(temperature, condition);
    }

}

