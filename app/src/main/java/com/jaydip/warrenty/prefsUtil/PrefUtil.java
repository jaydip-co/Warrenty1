package com.jaydip.warrenty.prefsUtil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.internal.$Gson$Preconditions;

public class PrefUtil {
    private static final String preference_name = "preference_Name";
    public static final String Default_Value = "Default_Value";
   public static void saveToPrivate(Context activity,String id,String value){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(preference_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(id,value);
        editor.commit();
//       Log.e("jaydip added",value);
    }
    public static String getPrefField(Context activity,String string){
       SharedPreferences sharedPreferences = activity.getSharedPreferences(preference_name, Context.MODE_PRIVATE);

       String value = sharedPreferences.getString(string,Default_Value);
       Log.e("jaydip in pref Util",value);
       return value;
    }
    ////////////////////////////////
    public static void setFinalPin(Context context){
       SharedPreferences sharedPreferences = context.getSharedPreferences(preference_name,Context.MODE_PRIVATE);
       SharedPreferences.Editor editor = sharedPreferences.edit();
       String temp_pin = getPrefField(context,prefIds.TEMP_PIN);
       if(!temp_pin.equals(Default_Value)){
           editor.putString(prefIds.FINAL_PIN,temp_pin);
           editor.commit();
       }
    }
}
