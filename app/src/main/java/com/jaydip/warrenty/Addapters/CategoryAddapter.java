package com.jaydip.warrenty.Addapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaydip.warrenty.ItemActivity;
import com.jaydip.warrenty.Listeners.DeleteCategory;
import com.jaydip.warrenty.Models.CategoryModel;
import com.jaydip.warrenty.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryAddapter extends RecyclerView.Adapter<CategoryAddapter.categoryHolder>{
    LayoutInflater inflater;
    Context context;
    List<CategoryModel> list;
    DeleteCategory listener;

    public CategoryAddapter(Context context){
        inflater = LayoutInflater.from(context);
        this.context = context;
        list = new ArrayList<>();
        listener = (DeleteCategory) context;
    }

    @NonNull
    @Override
    public categoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.single_category,parent,false);
        return new categoryHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull categoryHolder holder, final int position) {

        if(list != null) {

            CategoryModel single = list.get(position);
            holder.t1.setText(single.getCategoryName());
            String base = "@drawable/";
            String uri = base + single.getIconName();
            holder.totalCount.setText(String.valueOf(single.getTotalItem()));

            int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
            Drawable d = context.getDrawable(imageResource);
            holder.icon.setImageDrawable(d);


            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent(context, ItemActivity.class);
                    intent.putExtra("title", list.get(position).getCategoryName());
                    context.startActivity(intent);


                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {


                    Log.e("jaydip", "mode");
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("Do you want to delete Category "+single.getCategoryName()+" ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    listener.DeleteCCategory(single);

                                }
                            })
                            .setNegativeButton("Cancel", null);
                    builder.show();

                    return true;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  void setList(List<CategoryModel> models){
        this.list = models;
        notifyDataSetChanged();
    }

    class categoryHolder extends RecyclerView.ViewHolder{
        TextView t1,totalCount;
        CardView cardView;
        ImageView icon;


        public categoryHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.singleCAtegary);
            t1 = itemView.findViewById(R.id.categoryName);
            icon = itemView.findViewById(R.id.icon_image);
            totalCount = itemView.findViewById(R.id.totalCount);


        }
    }
}
