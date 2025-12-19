package com.example.expense_tracker_app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ExpenseResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<Expense> data;

    public boolean isSuccess() {
        return success;
    }

    public List<Expense> getData() {
        return data;
    }
}
