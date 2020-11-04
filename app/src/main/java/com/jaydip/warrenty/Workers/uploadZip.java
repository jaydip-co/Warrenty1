package com.jaydip.warrenty.Workers;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.jaydip.warrenty.App;
import com.jaydip.warrenty.R;
import com.jaydip.warrenty.Service.DriveServiceHelper;
import com.jaydip.warrenty.prefsUtil.PrefUtil;
import com.jaydip.warrenty.prefsUtil.prefIds;

import java.io.File;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class uploadZip extends Worker {
    DriveServiceHelper helper;
    NotificationManager manager;
    NotificationCompat.Builder mBuilder;


    public uploadZip(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);


        helper = new DriveServiceHelper(getApplicationContext());
        mBuilder = new NotificationCompat.Builder(getApplicationContext(), App.NOTIFICATI_ID);
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Result doWork() {
        mBuilder.setContentTitle("Uploading..")
                .setContentText("Upload Task")
                .setSmallIcon(R.drawable.backup);
        mBuilder.setProgress(100,50,false);
        manager.notify(110,mBuilder.build());
        File file = new File(getApplicationContext().getDataDir(),"jay.zip");
        helper.upload(file.getAbsolutePath(),"jay.zip","application/zip","appDataFolder")
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e("jaydiiiiip",s);
                        String newId = s;
                        String lastId = PrefUtil.getPrefField(getApplicationContext(), prefIds.LAST_BACKUP_ID);
                        Log.e("jaydip Last id", lastId+"ape 56");
                        mBuilder.setProgress(100,75,false);
                        manager.notify(110,mBuilder.build());
                        if(!lastId.equals(PrefUtil.Default_Value)){
                            helper.deleteBackup(lastId).addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                Log.e("jay","Deleted successFully");
                                PrefUtil.saveToPrivate(getApplicationContext(),prefIds.LAST_BACKUP_ID,newId);
                                mBuilder.setProgress(100,100,false)
                                .setContentTitle("Completed");
                                manager.notify(110,mBuilder.build());

                            }
                        });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("jaydippppp",e.toString());
                        e.printStackTrace();

                    }
                });
        return Result.success();
    }
}
