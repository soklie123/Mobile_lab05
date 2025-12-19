package com.example.expense_tracker_app.utils;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.expense_tracker_app.dao.AppDatabase;
import com.example.expense_tracker_app.dao.CategoryDao;
import com.example.expense_tracker_app.models.Category;

import java.util.List;
import java.util.concurrent.Executors;

public class CategoryViewModel extends AndroidViewModel {

    private final CategoryDao categoryDao;
    private final LiveData<List<Category>> allCategories;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        categoryDao = db.categoryDao();
        allCategories = categoryDao.getAllCategories();
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public void insert(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() ->
                categoryDao.insert(category)
        );
    }

}
