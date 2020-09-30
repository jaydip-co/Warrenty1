package com.jaydip.warrenty;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.preference.PreferenceManager;

import com.jaydip.warrenty.Broadcast.NotificationRecieverer;

import java.util.Calendar;

public class Utils {
    public static String NOTIFICATION_PRAF = "notification_switch";
    public static void setAlarm(Context context){

        Log.e("prefrence","reached");
        PackageManager pkgmanager = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, NotificationRecieverer.class);
        pkgmanager.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);

        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context,NotificationRecieverer.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,250,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,8);
        calendar.set(Calendar.MINUTE,30);
        calendar.set(Calendar.SECOND,0);
        Calendar now = Calendar.getInstance();
        if(calendar.getTimeInMillis() <= now.getTimeInMillis()){
            Log.e("jaydip","less");
            calendar.add(Calendar.DAY_OF_MONTH,1);
            Log.e("jaydip","less"+calendar.get(Calendar.DAY_OF_MONTH));

        }
//        context.sendBroadcast(intent);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }
    public static void cancelAlarm(Context context){
        PackageManager pkgmanager = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, NotificationRecieverer.class);
        pkgmanager.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context,NotificationRecieverer.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,250,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(pendingIntent);
    }
}
