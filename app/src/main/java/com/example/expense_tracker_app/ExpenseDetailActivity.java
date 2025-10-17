package com.example.expense_tracker_app;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.expense_tracker_app.models.Expense;
import com.example.expense_tracker_app.services.ExpenseService;
import com.google.android.material.appbar.MaterialToolbar;

public class ExpenseDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbarDetail);

        setSupportActionBar(toolbar);
        // Handle click on the navigation icon
        toolbar.setNavigationOnClickListener(v -> finish());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail_scrollview), (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find TextViews
        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvAmount = findViewById(R.id.tvDetailAmount);
        TextView tvDate = findViewById(R.id.tvDetailDate);
        TextView tvDesc = findViewById(R.id.tvDetailDesc);

        // Get expense
        ExpenseService expenseService = new ExpenseService();
        long expenseId = getIntent().getLongExtra("expense_id", -1);
        Expense expense = expenseService.getExpenseById((int) expenseId);

        if (expense != null) {
            tvName.setText(expense.getName());
            tvAmount.setText(String.format("$%s", expense.getAmount()));
            tvDate.setText(expense.getDate());
            tvDesc.setText(expense.getDescription());
        }
    }
}
