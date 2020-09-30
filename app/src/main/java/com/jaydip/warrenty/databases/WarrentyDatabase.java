package com.jaydip.warrenty.databases;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.jaydip.warrenty.Daos.CategoryDao;
import com.jaydip.warrenty.Daos.ItemDao;
import com.jaydip.warrenty.Models.CategoryModel;
import com.jaydip.warrenty.Models.ItemModel;
import com.jaydip.warrenty.ViewModels.CategoryViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
@Database(entities = {CategoryModel.class , ItemModel.class} , version = 2)
public abstract class WarrentyDatabase extends RoomDatabase {
    public static String DATABASENAME = "WarrentyDatabase";
    public abstract CategoryDao categoryDao();
    public abstract ItemDao itemDao();
    private static WarrentyDatabase INSTANCE;
    private static final int NUMBER_OF_THREAD = 4;
    public static final ExecutorService writeExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREAD);
    public static WarrentyDatabase getINSTANCE(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context,WarrentyDatabase.class,DATABASENAME).build();
            Log.i("jaydip","database Created");
        }
        return  INSTANCE;
    }

}
