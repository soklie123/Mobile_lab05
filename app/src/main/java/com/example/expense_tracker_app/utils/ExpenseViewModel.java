package com.example.expense_tracker_app.utils;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.expense_tracker_app.dao.AppDatabase;
import com.example.expense_tracker_app.dao.ExpenseDao;
import com.example.expense_tracker_app.models.Expense;

import java.util.List;

public class ExpenseViewModel extends AndroidViewModel {

    private ExpenseDao expenseDao;
    private LiveData<List<Expense>> allExpenses;

    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        expenseDao = database.expenseDao();
        allExpenses = expenseDao.getAllExpenses();
    }

    // Get all expenses
    public LiveData<List<Expense>> getAllExpenses() {
        return allExpenses;
    }

    // Insert expense (with receipt image URL)
    public void insert(Expense expense) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            expenseDao.insert(expense);
        });
    }

    // Update expense (with receipt image URL)
    public void update(Expense expense) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            expenseDao.update(expense);
        });
    }

    // Delete expense
    public void delete(Expense expense) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            expenseDao.delete(expense);
        });
    }

    // Get expense by ID
    public LiveData<Expense> getExpenseById(int id) {
        return expenseDao.getExpenseById(id);
    }

    // Get expenses by category
    public LiveData<List<Expense>> getExpensesByCategory(String category) {
        return expenseDao.getExpensesByCategory(category);
    }

    // Delete all expenses
    public void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            expenseDao.deleteAll();
        });
    }
}