package com.jaydip.warrenty.ViewModels;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.jaydip.warrenty.Daos.ItemDao;
import com.jaydip.warrenty.Models.ItemModel;
import com.jaydip.warrenty.databases.WarrentyDatabase;

import java.util.List;

public class ItemViewModel extends AndroidViewModel {
    WarrentyDatabase mDatabase;
    ItemDao mdao;
    private LiveData<List<ItemModel>> liveItems;
    private LiveData<List<ItemModel>> categoryWise;
    public ItemViewModel(@NonNull Application application) {
        super(application);
        mDatabase = WarrentyDatabase.getINSTANCE(getApplication().getApplicationContext());
        mdao = mDatabase.itemDao();
        liveItems = mdao.getAll();
    }
    //for all type of items
    public LiveData<List<ItemModel>> getLiveItems(){
        return liveItems;
    }

    //for live data of one category Items
    public LiveData<List<ItemModel>> getCategoryWise(String category){
        categoryWise = mdao.getCategoryWise(category);
        return categoryWise;
    }

    //for deleting items
    public void deleteItem(ItemModel item){
        WarrentyDatabase.writeExecutor.execute(()->{
            mdao.deleteItem(item);
        });
    }

    //for adding new items
    public void  addItem(ItemModel item){
        WarrentyDatabase.writeExecutor.execute(()->{
            mdao.addItem(item);
        });
    }
    public void update(ItemModel item){
        WarrentyDatabase.writeExecutor.execute(()->{
            mdao.update(item);
        });
    }
}
