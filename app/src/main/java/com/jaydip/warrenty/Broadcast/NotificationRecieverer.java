package com.jaydip.warrenty.Broadcast;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.room.Dao;

import com.jaydip.warrenty.App;
import com.jaydip.warrenty.Daos.ItemDao;
import com.jaydip.warrenty.ItemActivity;
import com.jaydip.warrenty.Models.ItemModel;
import com.jaydip.warrenty.R;
import com.jaydip.warrenty.databases.WarrentyDatabase;
import com.jaydip.warrenty.prefsUtil.PrefUtil;
import com.jaydip.warrenty.prefsUtil.prefIds;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.os.Build.VERSION_CODES.M;

public class NotificationRecieverer  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("jaydip","recieved to broadcast");
        WarrentyDatabase database = WarrentyDatabase.getINSTANCE(context);
        ItemDao dao = database.itemDao();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        WarrentyDatabase.writeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int leftDayT = PrefUtil.getPrefFieldInt(context, prefIds.ALARM_DAY);
                int leftDay = leftDayT == 0 ? 10 : leftDayT;
                
                List<ItemModel> models = dao.getModels();

                for(ItemModel model : models){
                    try {
                    Log.e("jaydip",model.getIname());
                    String expire = model.getExpireDate();
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

                        Date expireDate = format.parse(expire);
                        Calendar now = Calendar.getInstance();
                        Calendar expireCalande = Calendar.getInstance();
                        expireCalande.setTime(expireDate);
                        long def = expireCalande.getTimeInMillis() - now.getTimeInMillis();
                        int days = (int) TimeUnit.MILLISECONDS.toDays(Math.abs(def));


                    if(days < leftDay){
                        Intent intent1 = new Intent(context,ItemActivity.class);
                        intent.putExtra("title",model.getCategory());
                        PendingIntent pentingIntent = PendingIntent.getActivity(context,210,intent1,PendingIntent.FLAG_ONE_SHOT);


                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, App.NOTIFICATI_ID)
                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                .setSmallIcon(R.drawable.addcategory)
                                .setContentTitle("Reminder")
                                .setAutoCancel(true)
                                .setContentText("Warranty of "+model.getIname()+" expires  on " +model.getExpireDate());


                        byte[] imageByte = model.getItemImage();
                        if(imageByte != null){
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte,0,imageByte.length);
                            builder.setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap)
                            );

                        }
                        manager.notify(model.getKey(),builder.build());

                    }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}
