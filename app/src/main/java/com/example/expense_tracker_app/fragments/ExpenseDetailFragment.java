package com.example.expense_tracker_app.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.expense_tracker_app.R;
import com.example.expense_tracker_app.databinding.FragmentExpenseDetailBinding;
import com.example.expense_tracker_app.api.ExpenseApi;

import java.util.Locale;

public class ExpenseDetailFragment extends Fragment {

    private FragmentExpenseDetailBinding binding;
    private String expenseId;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentExpenseDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        // Toolbar setup
        binding.toolbarDetail.setTitle("Expense Detail");
        binding.toolbarDetail.setNavigationOnClickListener(v -> navController.popBackStack());

        // Read arguments
        Bundle args = getArguments();
        if (args == null) {
            Toast.makeText(requireContext(), "Error: Expense data is missing.", Toast.LENGTH_SHORT).show();
            navController.popBackStack();
            return;
        }

        expenseId = args.getString("expense_id");
        String category = args.getString("expense_category");
        String remark = args.getString("expense_remark");
        String description = args.getString("expense_description");
        double amount = args.getDouble("expense_amount", 0.0);
        String currency = args.getString("expense_currency");
        String dateTimeStr = args.getString("expense_date");
        String receiptImageUrl = args.getString("expense_receipt_image_url"); // NEW: Get receipt image URL

        // Split date and time
        String dateOnly = "N/A";
        String timeOnly = "N/A";

        if (!TextUtils.isEmpty(dateTimeStr)) {
            String[] parts = dateTimeStr.split(" ");
            dateOnly = parts[0];
            if (parts.length > 1) {
                timeOnly = parts[1];
            }
        }

        // Populate UI
        binding.tvDetailName.setText(!TextUtils.isEmpty(remark) ? remark : "N/A");
        binding.tvDetailDesc.setText(!TextUtils.isEmpty(description) ? description : "No description provided");
        binding.tvDetailAmount.setText(String.format(Locale.getDefault(), "%s%.2f", getCurrencySymbol(currency), amount));
        binding.tvDetailDate.setText(dateOnly);
        binding.tvDetailTime.setText(timeOnly);
        binding.tvDetailCategory.setText(!TextUtils.isEmpty(category) ? category : "N/A");

        // NEW: Display receipt image if available
        displayReceiptImage(receiptImageUrl);

        // Edit button
        binding.btnEditExpense.setOnClickListener(v -> {
            Bundle editArgs = new Bundle();
            editArgs.putBoolean("isEditMode", true);
            editArgs.putString("expense_id", expenseId);
            editArgs.putString("expense_category", category);
            editArgs.putString("expense_remark", remark);
            editArgs.putString("expense_description", description);
            editArgs.putDouble("expense_amount", amount);
            editArgs.putString("expense_currency", currency);
            editArgs.putString("expense_date", dateTimeStr);
            editArgs.putString("expense_receipt_image_url", receiptImageUrl); // Pass receipt URL

            navController.navigate(
                    R.id.action_expenseDetailFragment_to_addExpenseFragment,
                    editArgs
            );
        });

        // Delete button
        binding.btnDeleteExpense.setOnClickListener(v -> showDeleteConfirmationDialog(navController));
    }

    // NEW: Method to display receipt image
    private void displayReceiptImage(String receiptImageUrl) {
        if (!TextUtils.isEmpty(receiptImageUrl)) {
            binding.cardReceipt.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .load(receiptImageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_image) // Add placeholder image
                    .error(R.drawable.ic_image) // Add error image
                    .into(binding.imgReceipt);
        } else {
            binding.cardReceipt.setVisibility(View.GONE);
        }
    }

    private void showDeleteConfirmationDialog(NavController navController) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Expense")
                .setMessage("Do you want to delete this expense?")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("Ok", (dialog, which) -> {
                    if (TextUtils.isEmpty(expenseId)) {
                        Toast.makeText(requireContext(), "Expense ID missing", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    binding.btnDeleteExpense.setEnabled(false);
                    deleteExpenseAndFinish(navController);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteExpenseAndFinish(NavController navController) {
        ExpenseApi expenseApi = new ExpenseApi();
        expenseApi.deleteExpense(expenseId, new ExpenseApi.ApiCallback() {
            @Override
            public void onSuccess() {
                if (!isAdded()) return;

                // Notify HomeFragment to refresh list
                Bundle result = new Bundle();
                result.putBoolean("refresh", true);
                getParentFragmentManager().setFragmentResult("requestKey", result);

                Toast.makeText(requireContext(), "Expense deleted successfully", Toast.LENGTH_SHORT).show();
                navController.popBackStack();
            }

            @Override
            public void onFailure(String errorMessage) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Failed: " + errorMessage, Toast.LENGTH_LONG).show();
                if (binding != null) {
                    binding.btnDeleteExpense.setEnabled(true);
                }
            }
        });
    }

    private String getCurrencySymbol(String currency) {
        if (currency == null) return "";
        switch (currency) {
            case "USD":
                return "$";
            case "KHR":
                return "៛";
            default:
                return currency + " ";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}