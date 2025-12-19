package com.example.expense_tracker_app.fragments;

import android.os.Bundle;import android.view.LayoutInflater;
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
import androidx.navigation.fragment.NavHostFragment; // Correct import
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.expense_tracker_app.R;
import com.example.expense_tracker_app.adapters.ExpenseAdapter;
import com.example.expense_tracker_app.models.Expense;
import com.example.expense_tracker_app.api.ExpenseApi;
// We no longer need DateHelper here
// import com.example.expense_tracker_app.utils.DateHelper;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

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

        // SafeArea Insets
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return WindowInsetsCompat.CONSUMED;
        });

        recyclerView = view.findViewById(R.id.recyclerExpenseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize the adapter with the click listener
        adapter = new ExpenseAdapter(new ArrayList<>(), expense -> {
            // This is the implementation of the OnExpenseClickListener interface

            if (!isAdded()) return;

            Bundle args = new Bundle();
            args.putString("expense_id", expense.getId());
            args.putString("expense_category", expense.getCategory());
            args.putString("expense_remark", expense.getRemark());
            args.putString("expense_description", expense.getDescription());
            args.putDouble("expense_amount", expense.getAmount());
            args.putString("expense_currency", expense.getCurrency());

            // =========================================================
            // *** THE FIX IS HERE ***
            // Use the getDate() method which returns the "yyyy-MM-dd HH:mm:ss" string.
            // This is the same string your other fragments are designed to work with.
            args.putString("expense_date", expense.getDate());
            // =========================================================

            NavController navController = NavHostFragment.findNavController(HomeFragment.this);

            navController.navigate(
                    R.id.action_homeFragment_to_expenseDetailFragment,
                    args
            );
        });


        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this::loadExpenses);

        // Listen for results from other fragments (like after an edit/delete)
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            if (bundle.getBoolean("refresh")) {
                loadExpenses();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load expenses when the fragment becomes visible
        loadExpenses();
    }

    public void loadExpenses() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        ExpenseApi expenseApi = new ExpenseApi();
        expenseApi.getAllExpenses(new ExpenseApi.AllExpensesCallback() {
            @Override
            public void onSuccess(List<Expense> expenses) {
                // Ensure the fragment is still attached to an activity
                if (!isAdded() || getContext() == null) return;

                requireActivity().runOnUiThread(() -> {
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (adapter != null) {
                        adapter.setExpenseList(expenses);
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                if (!isAdded() || getContext() == null) return;

                requireActivity().runOnUiThread(() -> {
                    if (swipeRefreshLayout != null) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    Toast.makeText(
                            requireContext(),
                            "Load failed: " + errorMessage,
                            Toast.LENGTH_LONG
                    ).show();
                });
            }
        });
    }
}
