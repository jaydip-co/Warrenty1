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
import com.jaydip.warrenty.Addapters.itemAddapter;
import com.jaydip.warrenty.Listeners.ActivityForResult;
import com.jaydip.warrenty.Listeners.DeleteItem;
import com.jaydip.warrenty.Listeners.OnReceieCategory;
import com.jaydip.warrenty.Models.CategoryModel;
import com.jaydip.warrenty.Models.ItemModel;
import com.jaydip.warrenty.ViewModels.CategoryViewModel;
import com.jaydip.warrenty.ViewModels.ItemViewModel;

import java.util.ArrayList;
import java.util.List;

public class ItemActivity extends FragmentActivity implements DeleteItem , ActivityForResult , OnReceieCategory {
    List<ItemModel> items;
    itemAddapter Iaddapter;
    RecyclerView ItemView;
    ItemViewModel itemViewModel;
    String currentCategory;
    ImageView backButton;
    TextView title;
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
       Iaddapter = new itemAddapter(this,this,this);
       ItemView = findViewById(R.id.OneCategory);
       ItemView.setLayoutManager(new LinearLayoutManager(this));
       ItemView.setAdapter(Iaddapter);
       categoryModel = new CategoryViewModel(getApplication());
       addItem = findViewById(R.id.addItem);
       ///////////////////////////////////////////////////////



        itemViewModel.getCategoryWise(currentCategory).observe(this, new Observer<List<ItemModel>>() {
            @Override
            public void onChanged(List<ItemModel> itemModels) {
                Iaddapter.setList(itemModels);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_add,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.addItem){
            Intent intent = new Intent(this,AddItem.class);
            startActivity(intent);
            return true;

        }
        return super.onOptionsItemSelected(item);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("jaydip","result");
    }

    @Override
    public void satrtActivity(ItemModel model) {
        Intent intent = new Intent(getApplicationContext(), EditItem.class);
        intent.putExtra("item",model);
        startActivityForResult(intent,CODEFOREDIT);
    }

    @Override
    public void onReceive(CategoryModel model) {
        currentCategotryModel = model;
    }
}