package com.jaydip.warrenty;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaydip.warrenty.Models.ItemModel;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.util.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ViewFileActivity extends AppCompatActivity {
    ItemModel item;
    ImageView editButton,RecieptButton,backButton,ItemImage;
    TextView Iname,Icategory,purchasedate,expireDate,expireDateLabel,IMonth,IDetail,detailseperator,detailLable,lastUpdateDate,DaysLeft,daysLeftLabel,DaysLeftSeperator;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_file);
        Intent intent = getIntent();
        item = (ItemModel) intent.getSerializableExtra("item");
        editButton = findViewById(R.id.Iedit);
        backButton = findViewById(R.id.back_button);
        Iname = findViewById(R.id.Iname);
        Icategory = findViewById(R.id.Icategory);
        purchasedate = findViewById(R.id.purchasedate);
        expireDate = findViewById(R.id.expireDate);
        IMonth = findViewById(R.id.IMonth);
        IDetail = findViewById(R.id.IDetail);
        detailLable = findViewById(R.id.detailLable);
        detailseperator = findViewById(R.id.detailseperator);
        ItemImage = findViewById(R.id.Item_image);
        RecieptButton = findViewById(R.id.recieptButton);
        lastUpdateDate = findViewById(R.id.lastUpdateDate);
        DaysLeft = findViewById(R.id.IDaysLeft);
        daysLeftLabel = findViewById(R.id.daysLeft);
        DaysLeftSeperator = findViewById(R.id.DaysLeftSeperator);
        expireDateLabel = findViewById(R.id.expireDateLabel);



        //////////////////////////////////////////////////////

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(),EditItem.class);
                intent1.putExtra("item",item);
                startActivity(intent1);
                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setValue();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void setValue(){
        Iname.setText(item.getIname());
        Icategory.setText(item.getCategory());
        purchasedate.setText(item.getPurchaseDate());
        expireDate.setText(item.getExpireDate());
        IMonth.setText(item.getDurationMonth()+"");
        setLeftDays();


        if(item.getLastUpdateDate() != null){
            lastUpdateDate.setText(item.getLastUpdateDate());
        }
        if(item.getDetail().length() == 0){
            detailLable.setVisibility(View.GONE);
            detailseperator.setVisibility(View.GONE);
            IDetail.setVisibility(View.GONE);
        }
        else {
            IDetail.setText(item.getDetail());
        }
        if(item.getItemImage() != null){
//            ItemCard.setVisibility(View.VISIBLE);

            Bitmap ImageBit = BitmapFactory.decodeByteArray(item.getItemImage(),0,item.getItemImage().length);
            ItemImage.setImageBitmap(ImageBit);
            ItemImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),ImageActivity.class);
                    intent.putExtra("image",item.getItemImageUri());
                    startActivity(intent);
                }
            });
        }
        if(item.getBillUri() != null){
//            BillCard.setVisibility(View.VISIBLE);
//            Bitmap ImageBit = BitmapFactory.decodeByteArray(item.getBillImage(),0,item.getBillImage().length);
//            IBill.setImageBitmap(ImageBit);
//            IBill.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getApplicationContext(),ImageActivity.class);
//                    intent.putExtra("image",item.getBillUri());
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                }
//            });
            Log.e("jaydip is bill",item.isBillPdf()+"");
            if(item.isBillPdf()){
                RecieptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),pdfViewActivity.class);
                        intent.putExtra(pdfViewActivity.PDF_URI,item.getBillUri());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
//                        finish();
                    }
                });

            }
            else {
                RecieptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                        intent.putExtra(ImageActivity.KEY_FOR_IMAGE_URI, item.getBillUri());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
//                        finish();
                    }
                });
            }

        }



    }
     @RequiresApi(api = Build.VERSION_CODES.M)
     void setLeftDays() {
         try {


             String expire = item.getExpireDate();
             SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
             Date expireDatef = format.parse(expire);
             long def = Calendar.getInstance().getTime().getTime() - expireDatef.getTime();
             Log.e("jaydip",def+"");
             if(Calendar.getInstance().getTime().getTime() < expireDatef.getTime()){
                 int Left = (int)TimeUnit.MILLISECONDS.toDays(Math.abs(def));
                 DaysLeft.setText(String.valueOf(Left));
                 DaysLeftSeperator.setVisibility(View.VISIBLE);
                 DaysLeft.setVisibility(View.VISIBLE);
                 daysLeftLabel.setVisibility(View.VISIBLE);
             }
             else {
                 expireDateLabel.setText("Expired On");
                 expireDate.setTextColor(getColor(R.color.error));

             }




         }
         catch (Exception e){
             Log.e("jaydip",e.toString());
         }
     }


}