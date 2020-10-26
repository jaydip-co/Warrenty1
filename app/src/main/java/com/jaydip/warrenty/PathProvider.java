package com.jaydip.warrenty;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PathProvider {
    public static  File getOutputMediaFile(Context context,boolean val){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(context.getFilesDir()
                ,"/Files");
        String postfix = val ? "I" : "B";

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp+postfix+".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }
    public static File getnewPdf(Context context){
        File mediaDirectory = new File(context.getFilesDir(),"/pdfs");

        if(!mediaDirectory.exists()){
            if(!mediaDirectory.mkdirs()){
                return  null;
            }
        }

        String timestamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        String fileName = "pdf_"+timestamp+".pdf";
        File newFile = new File(mediaDirectory.getPath()+File.separator+fileName);
        return newFile;


    }
    public static void compressToZar(String _files, String zipFileName){
        try {
            int BUFFER = 1024;
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];


                Log.v("Compress", "Adding: " + _files);
                FileInputStream fi = new FileInputStream(_files);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry("files/"+_files.substring(_files.lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();


            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
