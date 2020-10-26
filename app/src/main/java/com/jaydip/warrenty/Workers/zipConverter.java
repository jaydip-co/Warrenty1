package com.jaydip.warrenty.Workers;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class zipConverter extends Worker {
    BufferedInputStream origin = null;
    ZipOutputStream stream;
    int Buffer = 1024;
    byte data[];

    public zipConverter(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Result doWork() {
        try {


            File file = new File(getApplicationContext().getDataDir(), "jay.zip");
            FileOutputStream dest = new FileOutputStream(file.getPath());
            stream = new ZipOutputStream(new BufferedOutputStream(dest));
             data = new byte[Buffer];
            Log.e("jaydip","adding");
            File databaseDir = new File(getApplicationContext().getDataDir(),"/databases");
            if(databaseDir.exists()){
               moveAlltoZip("databases",databaseDir);

            }
            File filesDir = new File(getApplicationContext().getFilesDir(),"Files");
            if(filesDir.exists()){
                moveAlltoZip("Files",filesDir);
            }
            File pdfDir = new File(getApplicationContext().getFilesDir(),"pdfs");
            if(pdfDir.exists()){
                moveAlltoZip("pdfs",pdfDir);
            }
            stream.close();
        }

        catch (Exception e){
            Log.e("jaydip",e.toString());
        }
        Log.e("jaydip","success");

    return Result.success();
    }
    void moveAlltoZip(String folderName,File file){
        try {
            File listOfFiles[] = file.listFiles();
            for (File f : listOfFiles) {
                FileInputStream fin = new FileInputStream(f);
                origin = new BufferedInputStream(fin,Buffer);

                ZipEntry entry = new ZipEntry(folderName+File.separator+f.getPath().substring(f.getPath().lastIndexOf("/")+1));
                stream.putNextEntry(entry);
                int count;
                while ((count = origin.read(data,0,Buffer))!= -1){
                    stream.write(data,0,count);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
