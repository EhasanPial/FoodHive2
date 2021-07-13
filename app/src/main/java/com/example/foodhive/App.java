package com.example.foodhive;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_ID = "FoodHive";
    public static String MSG = "";

    @Override

    public void onCreate() {
        super.onCreate();

        createChannel();

    }

    public static void setMessage(String Message) {
        MSG = Message;
    }

    private void createChannel() {
        // --------- Oreo er theke beshi hole Channel banabo -------- //

        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Food Hive", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 600, 1000});
            notificationChannel.setSound(ringtoneUri,audioAttributes);
            notificationChannel.setDescription("Food Hive");


            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);

        }
    }


}
