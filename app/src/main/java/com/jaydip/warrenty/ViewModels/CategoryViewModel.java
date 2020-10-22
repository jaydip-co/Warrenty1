package com.jaydip.warrenty.ViewModels;

import android.app.Application;

import com.jaydip.warrenty.Daos.CategoryDao;
import com.jaydip.warrenty.Listeners.OnReceieCategory;
import com.jaydip.warrenty.Models.CategoryModel;
import com.jaydip.warrenty.databases.WarrentyDatabase;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class CategoryViewModel extends AndroidViewModel {
    private WarrentyDatabase mdatabase;
    private CategoryDao mdao;
    private LiveData<List<CategoryModel>> listLiveData;
    private LiveData<List<String>> Allcategory;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        mdatabase = WarrentyDatabase.getINSTANCE(application.getApplicationContext());
        mdao = mdatabase.categoryDao();
        listLiveData = mdao.getAll();
    }

    //for getting all categoryModel
    public LiveData<List<CategoryModel>> getListLiveData(){

        return listLiveData;
    }

    //for getting all category name
    public LiveData<List<String>> getAllcategory() {
        Allcategory = mdao.getAllCategory();
        return Allcategory;
    }

    //to get single categoryModel
    public void getCategory(String category, OnReceieCategory listener){
        WarrentyDatabase.writeExecutor.execute(()->{
            CategoryModel cat = mdao.getCategory(category);
            listener.onReceive(cat);
        });
    }

    //to update categoryModel
    public void update(CategoryModel model){
        WarrentyDatabase.writeExecutor.execute(()->{
            mdao.updateCategory(model);
        });
    }

    //adding category
    public void addCategory(final CategoryModel model){
        mdatabase.writeExecutor.execute(()-> {
                mdao.addcategory(model);
        });
    }

    public void incrementItem(String category){
        WarrentyDatabase.writeExecutor.execute(()->{
            CategoryModel categoryModel = mdao.getCategory(category);
            int current = categoryModel.getTotalItem();
            current++;
            categoryModel.setTotalItem(current);
            mdao.updateCategory(categoryModel);
        });
    }
    public void decrementItem(String category){
        WarrentyDatabase.writeExecutor.execute(()->{
            CategoryModel categoryModel = mdao.getCategory(category);
            int current = categoryModel.getTotalItem();
            current--;
            categoryModel.setTotalItem(current);
            mdao.updateCategory(categoryModel);
        });
    }


}
