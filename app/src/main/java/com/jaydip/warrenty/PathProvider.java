package com.jaydip.warrenty;

import android.content.Context;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

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
}
