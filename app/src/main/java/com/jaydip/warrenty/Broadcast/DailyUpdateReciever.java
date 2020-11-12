package com.jaydip.warrenty.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.jaydip.warrenty.Workers.uploadZip;
import com.jaydip.warrenty.Workers.zipConverter;
import com.jaydip.warrenty.prefsUtil.PrefUtil;
import com.jaydip.warrenty.prefsUtil.prefIds;

public class DailyUpdateReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String value = PrefUtil.getPrefField(context, prefIds.Daily_update_Check);
        if(value.equals(PrefUtil.Default_Value)){
           Log.e("jaydip","no changes");
        }
        else {
            WorkManager manager = WorkManager.getInstance(context);
            OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(zipConverter.class).build();
            OneTimeWorkRequest request1 = new OneTimeWorkRequest.Builder(uploadZip.class).build();
            manager.beginWith(request).then(request1).enqueue();
            Log.e("jaydip", "updated reached changes");
        }
    }
}
