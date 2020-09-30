package com.jaydip.warrenty;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.util.Log;

public class App extends Application {
    public static  String NOTIFICATI_ID = "notification_id";
    @Override
    public void onCreate() {
        Log.e("jaydip","app");
        super.onCreate();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATI_ID,"Warrenty", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("for Warrenty");
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            Log.e("jaydip","channel created");
        }
    }
}
