package com.example.expense_tracker_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.expense_tracker_app.R;
import com.example.expense_tracker_app.adapters.ExpenseAdapter;
import com.example.expense_tracker_app.models.Expense;
import com.example.expense_tracker_app.api.ExpenseApi;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public static final String RESULT_EXPENSE_REFRESH = "expense_refresh";

    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Apply safe area insets
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return WindowInsetsCompat.CONSUMED;
        });

        recyclerView = view.findViewById(R.id.recyclerExpenseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize adapter with click listener
        adapter = new ExpenseAdapter(new ArrayList<>(), expense -> {
            if (!isAdded()) return;

            Bundle args = new Bundle();
            args.putString("expense_id", expense.getId());
            args.putString("expense_category", expense.getCategory());
            args.putString("expense_remark", expense.getRemark());
            args.putString("expense_description", expense.getDescription());
            args.putDouble("expense_amount", expense.getAmount());
            args.putString("expense_currency", expense.getCurrency());
            args.putString("expense_receipt_image_url", expense.getReceiptImageUrl());
            args.putString("expense_date", expense.getDate());

            NavController navController = NavHostFragment.findNavController(HomeFragment.this);
            navController.navigate(R.id.action_homeFragment_to_expenseDetailFragment, args);
        });

        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this::loadExpenses);

        // Listen for results from other fragments
        getParentFragmentManager().setFragmentResultListener(
                RESULT_EXPENSE_REFRESH,
                getViewLifecycleOwner(),
                (requestKey, bundle) -> {
                    if (bundle.getBoolean("refresh", false)) {
                        loadExpenses();
                    }
                }
        );

        // Initial load
        loadExpenses();

        return view;
    }

    private void loadExpenses() {
        swipeRefreshLayout.setRefreshing(true);

        ExpenseApi expenseApi = new ExpenseApi();
        expenseApi.getAllExpenses(new ExpenseApi.AllExpensesCallback() {
            @Override
            public void onSuccess(List<Expense> expenses) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    adapter.setExpenseList(expenses); // <-- update adapter data
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(requireContext(),
                            "Failed to load expenses: " + errorMessage,
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
