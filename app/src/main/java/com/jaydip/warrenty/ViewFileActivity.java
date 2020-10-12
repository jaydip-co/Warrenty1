package com.jaydip.warrenty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaydip.warrenty.Models.ItemModel;

public class ViewFileActivity extends AppCompatActivity {
    ItemModel item;
    ImageView editButton,backButton,IImage,IBill,ItemImage;
    TextView Iname,Icategory,purchasedate,expireDate,IMonth,IDetail,detailseperator,detailLable;
    CardView ItemCard,BillCard;

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
        IImage = findViewById(R.id.IImage);
        IBill = findViewById(R.id.IBill);
        ItemCard = findViewById(R.id.ItemCard);
        BillCard = findViewById(R.id.BillCard);
        ItemImage = findViewById(R.id.item_Image);



        //////////////////////////////////////////////////////

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(),EditItem.class);
                intent1.putExtra("item",item);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent1);
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
    void setValue(){
        Iname.setText(item.getIname());
        Icategory.setText(item.getCategory());
        purchasedate.setText(item.getPurchaseDate());
        expireDate.setText(item.getExpireDate());
        IMonth.setText(item.getDurationMonth()+"");
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
            IImage.setImageBitmap(ImageBit);
            ItemImage.setImageBitmap(ImageBit);
            IImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),ImageActivity.class);
                    intent.putExtra("image",item.getItemImageUri());
                    startActivity(intent);
                }
            });
        }
        if(item.getBillImage() != null){
//            BillCard.setVisibility(View.VISIBLE);
            Bitmap ImageBit = BitmapFactory.decodeByteArray(item.getBillImage(),0,item.getBillImage().length);
            IBill.setImageBitmap(ImageBit);
            IBill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),ImageActivity.class);
                    intent.putExtra("image",item.getBillUri());
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });

        }

    }
}