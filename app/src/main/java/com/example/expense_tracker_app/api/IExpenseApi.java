package com.example.expense_tracker_app.api;

import com.example.expense_tracker_app.models.Expense;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface IExpenseApi {

    // --- FIX: NO @Headers annotation and the return type is Call<ExpenseResponse> ---
    @GET("expenses")
    Call<List<Expense>> getExpenses();

    // --- FIX: NO @Headers annotation ---
    @POST("expenses")
    Call<Expense> createExpense(@Body Expense expense);

    // --- FIX: NO @Headers annotation ---
    @DELETE("expenses/{id}")
    Call<Void> deleteExpense(@Path("id") String id);

    // --- FIX: NO @Headers annotation ---
    @PUT("expenses/{id}")
    Call<Expense> updateExpense(@Path("id") String id, @Body Expense expense);
}
