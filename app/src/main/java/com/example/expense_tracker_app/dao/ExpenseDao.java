package com.example.expense_tracker_app.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.expense_tracker_app.models.Expense;

import java.util.List;

@Dao
public interface ExpenseDao {

    // Insert expense (returns the generated localId)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Expense expense);

    // Insert multiple expenses
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Expense> expenses);

    // Update expense (useful for editing expenses with images)
    @Update
    void update(Expense expense);

    // Delete expense by object
    @Delete
    void delete(Expense expense);

    // Delete expense by localId
    @Query("DELETE FROM expenses WHERE localId = :localId")
    void deleteById(int localId);

    // Delete all expenses
    @Query("DELETE FROM expenses")
    void deleteAll();

    // Get all expenses ordered by date
    @Query("SELECT * FROM expenses ORDER BY createdDate DESC")
    LiveData<List<Expense>> getAllExpenses();

    // Get single expense by localId (useful for detail view)
    @Query("SELECT * FROM expenses WHERE localId = :localId LIMIT 1")
    LiveData<Expense> getExpenseById(int localId);

    // Get single expense by server ID
    @Query("SELECT * FROM expenses WHERE id = :serverId LIMIT 1")
    LiveData<Expense> getExpenseByServerId(String serverId);

    // Get expenses by category
    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY createdDate DESC")
    LiveData<List<Expense>> getExpensesByCategory(String category);

    // Get unsynced expenses (for sync logic)
    @Query("SELECT * FROM expenses WHERE isSynced = 0")
    List<Expense> getUnsyncedExpenses();

    // Observe unsynced count (for Settings UI)
    @Query("SELECT COUNT(*) FROM expenses WHERE isSynced = 0")
    LiveData<Integer> getUnsyncedCount();

    // Mark expense as synced (useful after API upload)
    @Query("UPDATE expenses SET isSynced = 1, id = :serverId WHERE localId = :localId")
    void markAsSynced(int localId, String serverId);

    // Get expenses with receipt images only
    @Query("SELECT * FROM expenses WHERE receiptImageUrl IS NOT NULL AND receiptImageUrl != '' ORDER BY createdDate DESC")
    LiveData<List<Expense>> getExpensesWithReceipts();
}