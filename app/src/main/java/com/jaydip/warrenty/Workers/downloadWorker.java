package com.jaydip.warrenty.Workers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.webkit.WebHistoryItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.jaydip.warrenty.App;
import com.jaydip.warrenty.BackUpFragment;
import com.jaydip.warrenty.R;
import com.jaydip.warrenty.Service.DriveServiceHelper;
import com.jaydip.warrenty.prefsUtil.PrefUtil;
import com.jaydip.warrenty.prefsUtil.prefIds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class downloadWorker extends Worker {
    DriveServiceHelper helper;
    Context context;
    NotificationCompat.Builder builder;
    NotificationManager manager;


    public downloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(),
                Collections.singleton(DriveScopes.DRIVE_FILE));
        credential.setSelectedAccount(account.getAccount());
        Drive drive = new Drive.Builder(AndroidHttp.newCompatibleTransport(),new GsonFactory(),credential).build();
        helper = new DriveServiceHelper(getApplicationContext());
        builder = new NotificationCompat.Builder(context, App.NOTIFICATI_ID);
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
    void  sendProcess(String s,int progress){
        Intent intent  = new Intent(getApplicationContext().getString(R.string.intent_filter_for_process));
        intent.putExtra(BackUpFragment.Key_For_Status_String,s);
        intent.putExtra(BackUpFragment.Key_For_Status_process,progress);
        context.sendBroadcast(intent);

    }

    @NonNull
    @Override
    public Result doWork() {
        helper.getDetail().addOnSuccessListener(new OnSuccessListener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(String s) {
                sendProcess("started",0);

                File file = new File(getApplicationContext().getDataDir(),"down.zip");
               builder.setContentTitle("Downloading..")
                       .setContentText("download task")
                       .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);
               builder.setProgress(100,0,false);
               manager.notify(110,builder.build());
                sendProcess("Downloading..",50);
                helper.download(s,file.getPath()).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        builder.setProgress(100,50,false);
                        manager.notify(110,builder.build());
//                        try {
//                           Thread.sleep(3000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                        try {
                            byte[] Buffer = new byte[1024];

                            FileInputStream fin = new FileInputStream(file);
                            ZipInputStream zin = new ZipInputStream(fin);
                            ZipEntry entry = null;
                            while ((entry = zin.getNextEntry())!= null){
                                File file1 = getApplicationContext().getDataDir();
                                String taget = file1.getAbsolutePath()+"/jaydip";
                                File f = new File(taget);
                                if(!f.exists()){
                                    f.mkdir();
                                }
                                Log.e("zipEntry",entry.toString()+"  ");
                                if(entry.isDirectory()){
                                    File file2 =new File(getApplicationContext().getDataDir(),f+"/"+entry.getName());
                                    Log.e("zipEntry",file2.getAbsolutePath());
                                    if(!file2.exists()){
                                        file2.mkdir();
                                    }
                                }
                                String type = entry.getName().substring(0,entry.getName().indexOf("/"));
                                Log.e("jaydip data",entry.getName().substring(0,entry.getName().indexOf("/")));
                                String Fname = entry.getName().substring(entry.getName().lastIndexOf("/")+1);
                                switch (type){
                                    case "databases":
                                        File database = getApplicationContext().getDatabasePath(Fname);
                                        FileOutputStream stream = new FileOutputStream(database);
                                        int c;
                                        while ((c =zin.read(Buffer,0,1024))!= -1){
                                            stream.write(Buffer,0,c);
                                        }
                                        stream.close();
                                        Log.e("jaydip","same");
                                        break;
                                    case "Files":
                                        File files = new File(getApplicationContext().getFilesDir(),"Files");
                                        if(!files.exists()){
                                            files.mkdir();
                                        }
                                        File img = new File(files,Fname);
                                        FileOutputStream streamImage = new FileOutputStream(img);
                                        int d;
                                        while ((d = zin.read(Buffer,0,1024)) != -1){
                                            streamImage.write(Buffer,0,d);
                                        }
                                        streamImage.close();
                                        break;
                                    case "pdfs":
                                        File pdfDdir = new File(getApplicationContext().getFilesDir(),"pdfs");
                                        if(!pdfDdir.exists()){
                                            pdfDdir.mkdir();
                                        }
                                        File pdf = new File(pdfDdir,Fname);
                                        FileOutputStream pdfStream = new FileOutputStream(pdf);
                                        int e;
                                        while ((e = zin.read(Buffer,0,1024)) != -1){
                                            pdfStream.write(Buffer,0,e);

                                        }
                                        pdfStream.close();
                                }
//                                if(type.equals("databases")){
//                                    File Database = getApplicationContext().getDatabasePath(entry.getName().substring(entry.getName().indexOf("/")+1));
//                                    FileOutputStream stream = new FileOutputStream(Database);
//                                    int c;
//                                while ((c = zin.read(Buffer,0,1024)) != -1){
//                                    stream.write(Buffer,0,c);
//                                }
//                                stream.close();
//                                }
//                                FileOutputStream stream = new FileOutputStream(taget+"/"+entry.getName().substring(entry.getName().lastIndexOf("/")+1));
//
//                                int c;
//                                while ((c = zin.read(Buffer,0,1024)) != -1){
//                                    stream.write(Buffer,0,c);
//                                }
//                                stream.close();
                                    zin.closeEntry();

                            }
                            zin.close();
                            fin.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        builder.setContentTitle("Completed");
                        builder.setProgress(0,0,false);
                        manager.notify(110,builder.build());
                        sendProcess("Completed",100);
                        PrefUtil.saveToPrivate(getApplicationContext(), prefIds.INITIAL_UPDATE_DONE,"true");
                    }
                });

            }
        });
        return Result.success();
    }
}
