package com.jaydip.warrenty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jaydip.warrenty.Addapters.ExpiredItemAddapter;
import com.jaydip.warrenty.Addapters.itemAddapter;
import com.jaydip.warrenty.Listeners.ActivityForResult;
import com.jaydip.warrenty.Listeners.DeleteItem;
import com.jaydip.warrenty.Listeners.OnReceieCategory;
import com.jaydip.warrenty.Models.CategoryModel;
import com.jaydip.warrenty.Models.ItemModel;
import com.jaydip.warrenty.ViewModels.CategoryViewModel;
import com.jaydip.warrenty.ViewModels.ItemViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ItemActivity extends FragmentActivity implements DeleteItem , ActivityForResult , OnReceieCategory {
    List<ItemModel> items;
    itemAddapter Iaddapter;
    RecyclerView ItemView,expired;
    ItemViewModel itemViewModel;
    String currentCategory;
    ImageView backButton;
    TextView title,expiredTitle;
    Toolbar toolbar;
    CategoryViewModel categoryModel;
    CategoryModel currentCategotryModel;
    FloatingActionButton addItem;
    private static int CODEFOREDIT = 100;
    public static String KEY_FOR_CATEGORY = "category";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Log.e("jaydip","activity created");
        //////////////////////////////////////////////////
        Intent intent = getIntent();
        backButton = findViewById(R.id.back_button);
        title = findViewById(R.id.title);
        toolbar = findViewById(R.id.toolbar);

        setActionBar(toolbar);
        currentCategory = intent.getStringExtra("title");
        title.setText(currentCategory);
        items = new ArrayList<>();
       itemViewModel = new ItemViewModel(getApplication());
       Iaddapter = new itemAddapter(this);
       ItemView = findViewById(R.id.OneCategory);
       ItemView.setLayoutManager(new LinearLayoutManager(this));
       ItemView.setAdapter(Iaddapter);
       categoryModel = new CategoryViewModel(getApplication());
       addItem = findViewById(R.id.addItem);
       expired = findViewById(R.id.expiredRecycle);
       expired.setLayoutManager(new LinearLayoutManager(this));
        ExpiredItemAddapter expiredAddapter = new ExpiredItemAddapter(this);
       expired.setAdapter(expiredAddapter);
       expiredTitle = findViewById(R.id.expiredTitle);
      expired.setScrollContainer(false);
      ItemView.setScrollContainer(false);

       ///////////////////////////////////////////////////////



        itemViewModel.getCategoryWise(currentCategory).observe(this, new Observer<List<ItemModel>>() {

            int lastsize = 0;
            @Override
            public void onChanged(List<ItemModel> itemModels) {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                List<ItemModel> continueItem = new ArrayList<>();
                List<ItemModel> expiredItem = new ArrayList<>();
                for (ItemModel i : itemModels){
                    try {
                    String expire = i.getExpireDate();
                    Date expireDate = format.parse(expire);
                    if(Calendar.getInstance().getTime().getTime() < expireDate.getTime()){
                        continueItem.add(i);
                    }
                    else {
                        expiredItem.add(i);
                    }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }


                Iaddapter.setList(continueItem);
                if(expiredItem.size() > 0){
                    expiredTitle.setVisibility(View.VISIBLE);
                    expiredAddapter.setItems(expiredItem);
                }
                else {
                    expiredTitle.setVisibility(View.GONE);
                    expiredAddapter.setItems(expiredItem);

                }
//                expiredTitle.setVisibility(View.VISIBLE);
//                Iaddapter.setList(itemModels);
//                expiredAddapter.setItems(itemModels);

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        categoryModel.getCategory(currentCategory,this);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddItem.class);
                intent.putExtra(KEY_FOR_CATEGORY,currentCategory);
                startActivity(intent);
            }
        });
    }




    //listening for delete item
    @Override
    public void deleteItem(ItemModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Are You Sure?")
                .setCancelable(true)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        itemViewModel.deleteItem(model);
                        int count = currentCategotryModel.getTotalItem();
                        count--;
                        currentCategotryModel.setTotalItem(count);
                        categoryModel.update(currentCategotryModel);

                    }
                })
                .setNegativeButton("cancel",null);
        builder.show();
    }


    @Override
    public void satrtActivity(ItemModel model) {
        Intent intent = new Intent(getApplicationContext(), EditItem.class);
        intent.putExtra("item",model);
        startActivityForResult(intent,CODEFOREDIT);
    }

    @Override
    public void startshowActivity(ItemModel model) {
        Intent intent = new Intent(this,ViewFileActivity.class);
        intent.putExtra("item",model);
        startActivity(intent);
    }

    @Override
    public void startBillActivity(String Uri, boolean isBillPdf) {
        if(isBillPdf){
            Intent intent = new Intent(getApplicationContext(), pdfViewActivity.class);
            intent.putExtra(pdfViewActivity.PDF_URI,Uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else {
           Intent intent = new Intent(getApplicationContext(),ImageActivity.class);
           intent.putExtra(ImageActivity.KEY_FOR_IMAGE_URI,Uri);
           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           startActivity(intent);
        }
    }

    @Override
    public void onReceive(CategoryModel model) {
        currentCategotryModel = model;
    }
}