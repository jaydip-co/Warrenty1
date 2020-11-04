package com.jaydip.warrenty.Service;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.preference.Preference;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.jaydip.warrenty.SettingsActivity;
import com.jaydip.warrenty.prefsUtil.PrefUtil;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper {
    private  Drive mDriveService;
    private Executor mExecutor = Executors.newSingleThreadExecutor();
    Context context;
    public DriveServiceHelper(Context context){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if(account != null){
            Log.e("jaydip"," found aCCount");
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context,Collections.singleton(DriveScopes.DRIVE_APPDATA));
            credential.setSelectedAccount(account.getAccount());
            mDriveService = new Drive.Builder(AndroidHttp.newCompatibleTransport(),new GsonFactory(),credential)
                    .setApplicationName("Warranty").build();
        }
        else {
            Log.e("jaydip","Not found aCCount");
        }

//        this.mDriveService = mDriveService;
    }

    public Task<String> upload(String pathToStore,String fileName,String ContentType,String parent){
        return Tasks.call(mExecutor,()->{
            File fileMetadata = new File();
            fileMetadata.setName(fileName);
            fileMetadata.setParents(Collections.singletonList(parent));
            java.io.File fileToStore = new java.io.File(pathToStore);
            FileContent content = new FileContent(ContentType,fileToStore);
            File newCreatedFile = mDriveService.files().create(fileMetadata,content)
                    .setFields("id").execute();
            if(newCreatedFile == null){
                Log.e("jaydip",newCreatedFile.getId());

                return "Fail";
            }
            else{
                return newCreatedFile.getId();
            }
        });
    }
    public Task<String> getDetail(){
        return Tasks.call(mExecutor,()->{
            FileList list = mDriveService.files().list()
                    .setSpaces("appDataFolder")
                    .execute();
//            for (File f : list.getFiles()){
//                Log.e("jaydip",f.getId());
//                return f.getId();
//
//            }
            if(list.getFiles().size() > 0){
                File file = list.getFiles().get(0);

                return file.getId();
            }
            else {
                return "nothing";
            }

        });
    }
    public Task<String> download(String docId,String pathToStore){
        return Tasks.call(mExecutor,()->{
            java.io.File fileToStore = new java.io.File(pathToStore);
            OutputStream stream = new FileOutputStream(fileToStore);
             mDriveService.files().get(docId).executeMediaAndDownloadTo(stream);

            return "success";
        });
    }
    public Task<String> deleteBackup(String docId){
        return Tasks.call(mExecutor,()->{
           mDriveService.files().delete(docId).execute();
           return "true";


        });
    }
    public Task<String> printAllIDs(){
        return Tasks.call(mExecutor,()->{
            FileList list = mDriveService.files().list()
                    .setSpaces("appDataFolder").execute();
            for(File f : list.getFiles()){
                Log.e("jaydip Docid",f.getId());
            }
            return "true";
        });

    }

}
