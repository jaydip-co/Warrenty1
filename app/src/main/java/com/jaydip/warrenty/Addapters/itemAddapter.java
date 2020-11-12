package com.jaydip.warrenty.Addapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.jaydip.warrenty.ImageActivity;
import com.jaydip.warrenty.Listeners.ActivityForResult;
import com.jaydip.warrenty.Listeners.DeleteItem;
import com.jaydip.warrenty.Models.ItemModel;
import com.jaydip.warrenty.R;
import com.jaydip.warrenty.pdfViewActivity;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class itemAddapter extends RecyclerView.Adapter<itemAddapter.itemHolder> {

    List<ItemModel> list;
    LayoutInflater inflater;
    DeleteItem listener;
    Context context;
    ActivityForResult listener2;
    Drawable defaultImage;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public itemAddapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
        list = new ArrayList<>();
        this.listener = (DeleteItem) context;
        this.listener2 = (ActivityForResult) context;
        defaultImage = context.getDrawable(R.drawable.default_image);
    }
    @NonNull
    @Override
    public itemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.one_item,parent,false);
        return new itemHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull itemHolder holder, int position) {
        try {
        if(list!=null){
            ItemModel single = list.get(position);
            holder.t.setText(single.getIname());
            byte[] imageInbite = single.getItemImage();
            if(imageInbite != null){
                Bitmap image = BitmapFactory.decodeByteArray(imageInbite,0,imageInbite.length);
                holder.itemImage.setImageBitmap(image);
                Log.e("item",single.getItemImageUri());
                holder.itemImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      listener2.startBillActivity(single.getItemImageUri(),false);
                    }
                });

            }
            else {
                holder.itemImage.setImageDrawable(defaultImage);
                holder.itemImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
                holder.expireDate.setText(single.getExpireDate());
                holder.purchasedate.setText(single.getPurchaseDate());

                String expire = single.getExpireDate();
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                Date expireDate = format.parse(expire);
                Date currentdate = Calendar.getInstance().getTime();
                long def = expireDate.getTime() - currentdate.getTime();
                int daysLeft = (int)TimeUnit.MILLISECONDS.toDays(Math.abs(def));
                Log.e("jaydip","days left"+daysLeft);
                holder.daysLeft.setText(String.valueOf(daysLeft));




                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener2.satrtActivity(single);
                    }
                });

                holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.deleteItem(single);
                }
                 });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener2.startshowActivity(single);
                    }
                });
                if(single.getBillUri() != null){
                    holder.showBill.setVisibility(View.VISIBLE);
                   holder.showBill.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           listener2.startBillActivity(single.getBillUri(),single.isBillPdf());
                       }
                   });
                }
                else
                {
                    holder.showBill.setVisibility(View.GONE);
                }


        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void setList(List<ItemModel> models){
        this.list = models;
        notifyDataSetChanged();

    }

    class itemHolder extends RecyclerView.ViewHolder{
        TextView t,expireDate,daysLeft,purchasedate;
        ImageView itemImage,delete,edit,showBill;
        public itemHolder(@NonNull View itemView) {
            super(itemView);
            t = itemView.findViewById(R.id.ItemNAme);
            itemImage = itemView.findViewById(R.id.itemImage);
            expireDate = itemView.findViewById(R.id.expireDate);
            delete = itemView.findViewById(R.id.delete);
            daysLeft = itemView.findViewById(R.id.daysLeft);
            edit = itemView.findViewById(R.id.edit);
            purchasedate = itemView.findViewById(R.id.purchasedate);
            showBill = itemView.findViewById(R.id.ViewBill);
        }
    }
}
