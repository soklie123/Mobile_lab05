package com.example.expense_tracker_app.utils;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.expense_tracker_app.api.ApiClient;
import com.example.expense_tracker_app.api.IExpenseApi;
import com.example.expense_tracker_app.dao.AppDatabase;
import com.example.expense_tracker_app.dao.ExpenseDao;
import com.example.expense_tracker_app.models.Expense;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseRepository {

    private final IExpenseApi expenseApi;
    private final ExpenseDao expenseDao;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final Context context;

    /* ---------------- CALLBACK INTERFACE ---------------- */

    public interface IExpenseCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    /* ---------------- CONSTRUCTOR ---------------- */

    public ExpenseRepository(Application application) {
        this.context = application.getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(application);
        this.expenseDao = db.expenseDao();
        this.expenseApi = ApiClient.getClient().create(IExpenseApi.class);
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /* ---------------- LIVE DATA ---------------- */

    public LiveData<List<Expense>> getAllExpensesLiveData() {
        return expenseDao.getAllExpenses();
    }

    /* ---------------- REFRESH FROM API ---------------- */

    public void refreshExpensesFromApi(IExpenseCallback<Void> callback) {

        expenseApi.getExpenses().enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(
                    @NonNull Call<List<Expense>> call,
                    @NonNull Response<List<Expense>> response
            ) {
                if (response.isSuccessful() && response.body() != null) {

                    executorService.execute(() -> {
                        expenseDao.deleteAll();
                        expenseDao.insertAll(response.body());

                        mainHandler.post(() -> {
                            if (callback != null) callback.onSuccess(null);
                        });
                    });

                } else {
                    mainHandler.post(() -> {
                        if (callback != null) callback.onError(getErrorMessage(response));
                    });
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<List<Expense>> call,
                    @NonNull Throwable t
            ) {
                mainHandler.post(() -> {
                    if (callback != null) callback.onError("Network error: " + t.getMessage());
                });
            }
        });
    }

    /* ---------------- CREATE EXPENSE (OFFLINE-FIRST) ---------------- */

     public void createExpense(Expense expense, IExpenseCallback<Expense> callback) {

        // 🔹 STEP 1: Check network
        if (!NetworkUtil.isNetworkAvailable(context)) {
            // No internet → save locally
            saveExpenseLocally(expense, callback);
            return;
        }

        // 🔹 STEP 2: Internet available → try API
        expenseApi.createExpense(expense).enqueue(new Callback<Expense>() {

            @Override
            public void onResponse(
                    @NonNull Call<Expense> call,
                    @NonNull Response<Expense> response
            ) {
                if (response.isSuccessful() && response.body() != null) {

                    executorService.execute(() -> {
                        Expense serverExpense = response.body();
                        serverExpense.setSynced(true);
                        expenseDao.insert(serverExpense);

                        mainHandler.post(() -> callback.onSuccess(serverExpense));
                    });

                } else {
                    // API failed → fallback to local
                    saveExpenseLocally(expense, callback);
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<Expense> call,
                    @NonNull Throwable t
            ) {
                // Network dropped → fallback
                saveExpenseLocally(expense, callback);
            }
        });
    }


    private void saveExpenseLocally(Expense expense, IExpenseCallback<Expense> callback) {
        executorService.execute(() -> {
            expense.setSynced(false);
            long id = expenseDao.insert(expense);
            expense.setLocalId((int) id);

            mainHandler.post(() -> callback.onSuccess(expense));
        });
    }

    /* ---------------- SYNC UNSYNCED EXPENSES ---------------- */

    public void syncExpenses(IExpenseCallback<Void> callback) {

        // 🔹 STEP 1: Check network first
        if (!NetworkUtil.isNetworkAvailable(context)) {
            mainHandler.post(() ->
                    callback.onError("No internet connection")
            );
            return;
        }

        executorService.execute(() -> {

            List<Expense> unsynced = expenseDao.getUnsyncedExpenses();

            if (unsynced.isEmpty()) {
                mainHandler.post(() -> callback.onSuccess(null));
                return;
            }

            AtomicInteger counter = new AtomicInteger(unsynced.size());
            AtomicInteger errors = new AtomicInteger(0);

            for (Expense expense : unsynced) {

                expenseApi.createExpense(expense).enqueue(new Callback<Expense>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<Expense> call,
                            @NonNull Response<Expense> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {

                            executorService.execute(() -> {
                                expenseDao.deleteById(expense.getLocalId());
                                Expense serverExpense = response.body();
                                serverExpense.setSynced(true);
                                expenseDao.insert(serverExpense);
                            });

                        } else {
                            errors.incrementAndGet();
                        }

                        finishSync(counter, errors, callback);
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Expense> call,
                            @NonNull Throwable t
                    ) {
                        errors.incrementAndGet();
                        finishSync(counter, errors, callback);
                    }
                });
            }
        });
    }


    private void finishSync(
            AtomicInteger counter,
            AtomicInteger errors,
            IExpenseCallback<Void> callback
    ) {
        if (counter.decrementAndGet() == 0) {
            mainHandler.post(() -> {
                if (errors.get() > 0)
                    callback.onError(errors.get() + " expenses failed to sync");
                else
                    callback.onSuccess(null);
            });
        }
    }

    /* ---------------- ERROR HANDLER ---------------- */

    private String getErrorMessage(Response<?> response) {
        if (response.errorBody() != null) {
            try {
                return response.code() + " - " + response.errorBody().string();
            } catch (IOException e) {
                Log.e("ExpenseRepository", "Error parsing error body", e);
            }
        }
        return response.code() + " " + response.message();
    }
}
