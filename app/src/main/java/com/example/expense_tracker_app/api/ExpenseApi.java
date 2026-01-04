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

    // --- Callback for single operation (create, update, delete)
    public interface ApiCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    // --- Callback for fetching all expenses
    public interface AllExpensesCallback {
        void onSuccess(List<Expense> expenses);
        void onFailure(String errorMessage);
    }

    // Constructor: initialize Retrofit service
    public ExpenseApi() {
        service = ApiClient.getClient().create(IExpenseApi.class);
    }

    // --- CREATE Expense ---
    public void createExpense(Expense expense, ApiCallback callback) {
        service.createExpense(expense).enqueue(new Callback<Expense>() {
            @Override
            public void onResponse(@NonNull Call<Expense> call, @NonNull Response<Expense> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess();
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

    // --- READ ALL Expenses ---
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

    // --- UPDATE Expense ---
    public void updateExpense(String expenseId, Expense expense, ApiCallback callback) {
        service.updateExpense(expenseId, expense).enqueue(new Callback<Expense>() {
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
                            e.printStackTrace();
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

    // --- DELETE Expense ---
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
}
