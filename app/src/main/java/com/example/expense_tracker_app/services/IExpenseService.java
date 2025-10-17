package com.example.expense_tracker_app.services;

import com.example.expense_tracker_app.models.Expense;
import java.util.List;

public interface IExpenseService {
    List<Expense> getRecentExpenses();
}
