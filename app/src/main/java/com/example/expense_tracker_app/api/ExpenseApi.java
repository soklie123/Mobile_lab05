package com.example.expense_tracker_app.api;

import androidx.annotation.NonNull;

import com.example.expense_tracker_app.models.Expense;

import java.io.IOException;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseApi {

    private final IExpenseApi service;

    public interface ApiCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface AllExpensesCallback {
        void onSuccess(List<Expense> expenses);
        void onFailure(String errorMessage);
    }

    public ExpenseApi() {
        service = ApiClient.getClient().create(IExpenseApi.class);
    }

    // --- Corrected saveExpense method ---
    public void createExpense(Expense expense, ApiCallback callback) {
        // Your IExpenseApi says this returns Call<Expense>
        service.createExpense(expense).enqueue(new Callback<Expense>() { // <-- This MUST be <Expense>
            @Override
            public void onResponse(@NonNull Call<Expense> call, @NonNull Response<Expense> response) {
                // If the response is successful, the body contains the new Expense object.
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(); // The operation was a success.
                } else {
                    String errorMsg = "Failed to save. Code: " + response.code();
                    callback.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Expense> call, @NonNull Throwable t) {
                callback.onFailure("Network Failure: " + t.getMessage());
            }
        });
    }

    // --- Corrected getAllExpenses method ---
    // --- NEW, CORRECT VERSION for ExpenseApi.java ---
    // In file: ExpenseApi.java
    public void getAllExpenses(AllExpensesCallback callback) {
        service.getExpenses().enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(@NonNull Call<List<Expense>> call, @NonNull Response<List<Expense>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Failed to load expenses. Code: " + response.code();
                    callback.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Expense>> call, @NonNull Throwable t) {
                callback.onFailure("Network Failure: " + t.getMessage());
            }
        });
    }

    public void deleteExpense(String expenseId, ApiCallback callback) {
        service.deleteExpense(expenseId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure("Failed to delete. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onFailure("Network Failure: " + t.getMessage());
            }
        });
    }

    public void updateExpense(String expenseId, Expense expense, ApiCallback callback) {
        // Your IExpenseApi says this returns Call<Expense>
        service.updateExpense(expenseId, expense).enqueue(new Callback<Expense>() { // <-- This MUST be <Expense>
            @Override
            public void onResponse(@NonNull Call<Expense> call, @NonNull Response<Expense> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess();
                } else {
                    String errorMsg = "Failed to update. Code: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMsg += ": " + response.errorBody().string();
                        } catch (IOException e) {
                            android.util.Log.e("ExpenseApi", "Error reading error body", e);
                        }
                    }
                    callback.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Expense> call, @NonNull Throwable t) {
                callback.onFailure("Network Failure: " + t.getMessage());
            }
        });
    }
}
