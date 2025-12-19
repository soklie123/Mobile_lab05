package com.example.expense_tracker_app.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.expense_tracker_app.models.Expense;

import java.util.List;

@Dao
public interface ExpenseDao {

    // Get unsynced expenses (for sync logic)
    @Query("SELECT * FROM expenses WHERE isSynced = 0")
    List<Expense> getUnsyncedExpenses();

    // Observe unsynced count (for Settings UI)
    @Query("SELECT COUNT(*) FROM expenses WHERE isSynced = 0")
    LiveData<Integer> getUnsyncedCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Expense expense);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Expense> expenses);

    @Query("DELETE FROM expenses WHERE localId = :localId")
    void deleteById(int localId);

    @Query("DELETE FROM expenses")
    void deleteAll();

    @Query("SELECT * FROM expenses ORDER BY createdDate DESC")
    LiveData<List<Expense>> getAllExpenses();
}

