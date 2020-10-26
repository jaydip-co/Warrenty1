package com.jaydip.warrenty.Workers;

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
import com.jaydip.warrenty.Service.DriveServiceHelper;

import java.io.File;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class uploadZip extends Worker {
    DriveServiceHelper helper;


    public uploadZip(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account != null){
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                    getApplicationContext(),
                    Collections.singleton(DriveScopes.DRIVE_FILE)
            );
            credential.setSelectedAccount(account.getAccount());
            Drive drive = new Drive.Builder(AndroidHttp.newCompatibleTransport(),new GsonFactory(),credential)
                    .setApplicationName("Warrenty")
                    .build();
            helper = new DriveServiceHelper(drive);


        }


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Result doWork() {
        File file = new File(getApplicationContext().getDataDir(),"jay.zip");
        helper.upload(file.getAbsolutePath(),"jay.zip","application/zip","root")
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e("jaydip",s);
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
