package com.jaydip.warrenty;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.jaydip.warrenty.Broadcast.NotificationRecieverer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int hour = preferences.getInt(SettingsActivity.key_time_hour,8);
        int minute = preferences.getInt(SettingsActivity.key_time_Minute,30);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.SECOND,0);
        Calendar now = Calendar.getInstance();
        if(calendar.getTimeInMillis() <= now.getTimeInMillis()){
            Log.e("jaydip","less");
            calendar.add(Calendar.DAY_OF_MONTH,1);
            Log.e("jaydip","less"+calendar.get(Calendar.DAY_OF_MONTH));

        }
        Log.e("jaydip","setted on"+hour+":"+minute);
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


    public static void copyPdf(Context context,String oldpath,String newPath){
        try {


            File oldFile = new File(oldpath);
            File newFile = new File(newPath);

            InputStream IStream = new FileInputStream(oldFile);
            OutputStream OStream = new FileOutputStream(newFile);
            byte[] buff = new byte[1024];
            int len;
            while ((len = IStream.read(buff)) > 0){
                OStream.write(buff,0,len);
            }
            IStream.close();
            OStream.close();
        }
        catch (Exception e){
            Log.e("pdf",e.toString());
        }

    }

    public static String storeToTemp(Context context,Bitmap bitmap,boolean isItem) throws IOException {

        File mediaStore = new File(context.getFilesDir()+"/temp");
        if(!mediaStore.exists()){
            if(!mediaStore.mkdirs()){
                return null;
            }
        }
        String postFix;
        if(isItem){
            postFix = "I";
        }
        else {
            postFix = "B";
        }

        String fileName = "tmp_"+postFix+".jpg";
        File newfile = new File(mediaStore.getPath()+ File.separator+fileName);
        FileOutputStream outputStream = new FileOutputStream(newfile);
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        outputStream.close();
        return newfile.getPath();
    }
}
