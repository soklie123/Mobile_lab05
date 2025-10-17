package com.example.expense_tracker_app.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Add ViewCompat and Insets imports
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.expense_tracker_app.ExpenseDetailActivity;
import com.example.expense_tracker_app.R;
import com.example.expense_tracker_app.adapters.ExpenseAdapter;
import com.example.expense_tracker_app.models.Expense;
import com.example.expense_tracker_app.services.ExpenseService;

import java.util.List;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // --- View Insets Application ---
        // Apply insets to the RecyclerView so its padding is adjusted
        // and its content doesn't get hidden under the system bars (status bar/navigation bar).
        // Using the RecyclerView itself ensures the padding is applied correctly to the scrollable area.
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Apply padding to the *root view* of the fragment (or a specific container)
            // to move the content away from the status bar.
            // Note: Since MainActivity handles the bottom nav, here we primarily focus on the top.
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);

            // You can also adjust the RecyclerView's clipToPadding if needed, but often
            // just setting the padding on the root view or the RecyclerView itself is enough.

            return WindowInsetsCompat.CONSUMED;
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerExpenseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ExpenseService expenseService = new ExpenseService();

        // Load data and set up adapter
        List<Expense> expenseList = expenseService.getAllExpenses();

        ExpenseAdapter adapter = new ExpenseAdapter(expenseList, expense -> {
            Intent intent = new Intent(getContext(), ExpenseDetailActivity.class);
            intent.putExtra("expense_id", expense.getId());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        return view;
    }
}