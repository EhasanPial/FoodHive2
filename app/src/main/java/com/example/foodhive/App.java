package com.example.foodhive;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_ID = "FoodHive";
    public static String MSG = "" ;
    @Override

    public void onCreate() {
        super.onCreate();

        createChannel();

    }

    public static void setMessage(String Message) {
          MSG = Message ;
    }

    private void createChannel() {
        // --------- Oreo er theke beshi hole Channel banabo -------- //

        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Food Hive", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.setDescription("Food Hive");


            NotificationManager notificationManager = getSystemService(NotificationManager.class) ;
            notificationManager.createNotificationChannel(notificationChannel);

        }
    }




}
