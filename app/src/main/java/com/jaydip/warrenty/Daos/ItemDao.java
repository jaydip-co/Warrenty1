package com.jaydip.warrenty.Daos;

import com.jaydip.warrenty.Models.ItemModel;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ItemDao {
    @Insert
    void addItem(ItemModel item);

    @Query("SELECT * FROM Items")
    List<ItemModel> getModels();

    @Query("SELECT * FROM Items")
    LiveData<List<ItemModel>> getAll();

    @Query("SELECT * FROM Items WHERE Category = :category")
    LiveData<List<ItemModel>> getCategoryWise(String category);
    @Delete
    public void deleteItem(ItemModel model);

    @Update
    public  void update(ItemModel model);

    @Query("Delete FROM Items WHERE Category = :category")
    void deleteWithCategory(String category);
}
