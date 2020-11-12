package com.jaydip.warrenty.Addapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.jaydip.warrenty.Listeners.ActivityForResult;
import com.jaydip.warrenty.Listeners.DeleteItem;
import com.jaydip.warrenty.Models.ItemModel;
import com.jaydip.warrenty.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExpiredItemAddapter extends RecyclerView.Adapter<ExpiredItemAddapter.ExpiredItemHolder> {

    LayoutInflater inflater;
    List<ItemModel> items;
    DeleteItem listener;
    Context context;
    ActivityForResult listener2;

    public ExpiredItemAddapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
        items = new ArrayList<>();
        this.listener = (DeleteItem) context;
        this.listener2 = (ActivityForResult) context;

    }
    @NonNull
    @Override
    public ExpiredItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.one_expired_item,parent,false);
        return new ExpiredItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpiredItemHolder holder, int position) {
        try {
            if(items!=null){
                ItemModel single = items.get(position);
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

                holder.expireDate.setText(single.getExpireDate());
                holder.purchasedate.setText(single.getPurchaseDate());

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
        return items.size();
    }
    public void setItems(List<ItemModel> items){
        this.items = items;
        notifyDataSetChanged();
    }

    class ExpiredItemHolder extends RecyclerView.ViewHolder{
        TextView t,expireDate,purchasedate;
        ImageView itemImage,delete,edit,showBill;
        public ExpiredItemHolder(@NonNull View itemView) {
            super(itemView);
            t = itemView.findViewById(R.id.ItemNAme);
            itemImage = itemView.findViewById(R.id.itemImage);
            expireDate = itemView.findViewById(R.id.expireDate);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
            purchasedate = itemView.findViewById(R.id.purchasedate);
            showBill = itemView.findViewById(R.id.ViewBill);
        }
    }
}
