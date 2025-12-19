package com.example.expense_tracker_app.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.expense_tracker_app.api.ExpenseApi;
import com.example.expense_tracker_app.databinding.FragmentAddExpenseBinding;
import com.example.expense_tracker_app.models.Category;
import com.example.expense_tracker_app.models.Expense;
import com.example.expense_tracker_app.utils.CategoryViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AddExpenseFragment extends Fragment {

    private FragmentAddExpenseBinding binding;
    private FirebaseAuth mAuth;

    private boolean isEditMode = false;
    private String existingExpenseId = null;

    private CategoryViewModel categoryViewModel;
    private ArrayAdapter<String> categoryAdapter;
    private final List<String> categoryNames = new ArrayList<>();

    // Default categories to insert if Room is empty
    private final List<String> originalCategories = Arrays.asList(
            "Groceries",
            "Restaurants"
    );

    private final Calendar selectedDateTime = Calendar.getInstance();
    private final SimpleDateFormat storageFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public interface OnExpenseAddedListener {
        void onExpenseAdded();
    }

    private OnExpenseAddedListener listener;

    public void setOnExpenseAddedListener(OnExpenseAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddExpenseBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categoryViewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);

        insertDefaultCategoriesIfNeeded();
        setupToolbar();
        setupCategoryDropdown();
        setupDatePicker();

        binding.btnAddCategory.setOnClickListener(v -> showAddCategoryDialog());
        binding.btnSaveExpense.setOnClickListener(v -> saveOrUpdateExpense());

        Bundle args = getArguments();
        if (args != null && args.getBoolean("isEditMode", false)) {
            isEditMode = true;
            existingExpenseId = args.getString("expense_id");
            populateFormForEdit(args);
        } else {
            isEditMode = false;
            selectedDateTime.setTime(new Date());
            updateDateDisplay();
        }
    }

    // -------------------- CATEGORY --------------------

    private void insertDefaultCategoriesIfNeeded() {
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories == null || categories.isEmpty()) {
                for (String name : originalCategories) {
                    categoryViewModel.insert(new Category(name));
                }
            }
        });
    }

    // Setup Category Drop Drown
    // Setup Category Drop Drown
    private void setupCategoryDropdown() {
        // The adapter will now be created and set inside the LiveData observer.
        // This ensures it's fresh after a configuration change.

        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categoriesFromDb -> {
            Set<String> uniqueNames = new LinkedHashSet<>();

            // Always include defaults
            uniqueNames.addAll(originalCategories);

            // Include all Room categories
            if (categoriesFromDb != null) {
                for (Category c : categoriesFromDb) {
                    if (!TextUtils.isEmpty(c.getName())) {
                        uniqueNames.add(c.getName());
                    }
                }
            }

            // Create a new list for the adapter from the unique names.
            List<String> updatedCategoryNames = new ArrayList<>(uniqueNames);

            // Create a new ArrayAdapter with the fresh data.
            ArrayAdapter<String> newAdapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    updatedCategoryNames
            );

            // Set the new adapter on the AutoCompleteTextView.
            binding.spinnerCategory.setAdapter(newAdapter);

            // Keep the fragment's own list in sync if you use it elsewhere (e.g., in showAddCategoryDialog)
            this.categoryNames.clear();
            this.categoryNames.addAll(updatedCategoryNames);
            this.categoryAdapter = newAdapter;
        });
    }



    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Category");

        final EditText input = new EditText(requireContext());
        input.setHint("Enter category name");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newCategoryName = input.getText().toString().trim();

            if (TextUtils.isEmpty(newCategoryName)) return;

            // Check if already exists in dropdown
            for (String existingName : categoryNames) {
                if (existingName.equalsIgnoreCase(newCategoryName)) {
                    Toast.makeText(getContext(), "Category already exists", Toast.LENGTH_SHORT).show();
                    binding.spinnerCategory.setText(existingName, false);
                    return;
                }
            }

            // Insert into Room if unique
            Category newCategory = new Category(newCategoryName);
            categoryViewModel.insert(newCategory);
            binding.spinnerCategory.setText(newCategoryName, false); // select immediately
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    // -------------------- DATE & TIME --------------------

    private void setupDatePicker() {
        binding.editDate.setOnClickListener(v -> {
            int year = selectedDateTime.get(Calendar.YEAR);
            int month = selectedDateTime.get(Calendar.MONTH);
            int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
                selectedDateTime.set(Calendar.YEAR, selectedYear);
                selectedDateTime.set(Calendar.MONTH, selectedMonth);
                selectedDateTime.set(Calendar.DAY_OF_MONTH, selectedDay);
                showTimePicker();
            }, year, month, day).show();
        });
    }

    private void showTimePicker() {
        int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDateTime.get(Calendar.MINUTE);

        new TimePickerDialog(requireContext(), (view, hourOfDay, minuteOfHour) -> {
            selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedDateTime.set(Calendar.MINUTE, minuteOfHour);
            selectedDateTime.set(Calendar.SECOND, 0);
            updateDateDisplay();
        }, hour, minute, true).show();
    }

    private void updateDateDisplay() {
        binding.editDate.setText(formatWithOrdinal(selectedDateTime.getTime()));
    }

    private String formatWithOrdinal(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String daySuffix = getDayOfMonthSuffix(day);
        SimpleDateFormat displayFormat = new SimpleDateFormat(" MMM yyyy, HH:mm", Locale.getDefault());
        return day + daySuffix + displayFormat.format(date);
    }

    private String getDayOfMonthSuffix(final int n) {
        if (n >= 11 && n <= 13) return "th";
        switch (n % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }

    // -------------------- TOOLBAR --------------------

    private void setupToolbar() {
        binding.addToolbar.setNavigationOnClickListener(v -> {
            if (isAdded()) Navigation.findNavController(requireView()).popBackStack();
        });
    }

    // -------------------- FORM --------------------

    private void populateFormForEdit(Bundle args) {
        binding.addToolbar.setTitle("Edit Expense");
        binding.btnSaveExpense.setText("Update Expense");

        String remark = args.getString("expense_remark");
        double amount = args.getDouble("expense_amount");
        String description = args.getString("expense_description");
        String category = args.getString("expense_category");
        String dateString = args.getString("expense_date");

        binding.editFoodName.setText(remark);
        binding.editAmount.setText(String.format(Locale.US, "%.2f", amount));
        binding.editDescription.setText(description);
        binding.spinnerCategory.setText(category, false);

        if (dateString != null && !dateString.isEmpty()) {
            try {
                Date storedDate = storageFormat.parse(dateString);
                selectedDateTime.setTime(Objects.requireNonNull(storedDate));
            } catch (ParseException e) {
                Log.e("AddExpenseFragment", "Failed to parse date", e);
                selectedDateTime.setTime(new Date());
            }
        }
        updateDateDisplay();
    }

    private void saveOrUpdateExpense() {
        String remark = Objects.requireNonNull(binding.editFoodName.getText()).toString().trim();
        String amountStr = Objects.requireNonNull(binding.editAmount.getText()).toString().trim();
        String category = binding.spinnerCategory.getText().toString().trim();
        String description = Objects.requireNonNull(binding.editDescription.getText()).toString().trim();

        if (TextUtils.isEmpty(remark) || TextUtils.isEmpty(amountStr) || TextUtils.isEmpty(category)) {
            Toast.makeText(getContext(), "Remark, Amount, and Category are required", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.layoutAmount.setError(null);
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            binding.layoutAmount.setError("Invalid number");
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Authentication error. Please re-login.", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateToStore = storageFormat.format(selectedDateTime.getTime());

        Expense expense = new Expense();
        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setRemark(remark);
        expense.setDescription(description);
        expense.setDate(dateToStore);
        expense.setCreatedBy(user.getUid());
        expense.setCurrency("USD");

        binding.btnSaveExpense.setEnabled(false);
        ExpenseApi expenseApi = new ExpenseApi();

        if (isEditMode) {
            expenseApi.updateExpense(existingExpenseId, expense, createApiCallback(true));
        } else {
            expenseApi.createExpense(expense, createApiCallback(false));
        }
    }

    private ExpenseApi.ApiCallback createApiCallback(boolean isUpdate) {
        return new ExpenseApi.ApiCallback() {
            @Override
            public void onSuccess() {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    String message = isUpdate ? "Expense updated successfully" : "Expense added successfully";
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                    if (isUpdate) {
                        Bundle result = new Bundle();
                        result.putBoolean("refresh", true);
                        getParentFragmentManager().setFragmentResult("requestKey", result);
                        Navigation.findNavController(requireView()).popBackStack();
                    } else {
                        clearForm();
                        binding.btnSaveExpense.setEnabled(true);
                        if (listener != null) listener.onExpenseAdded();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    String message = (isUpdate ? "Update" : "Save") + " failed: " + errorMessage;
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    binding.btnSaveExpense.setEnabled(true);
                });
            }
        };
    }

    private void clearForm() {
        binding.editFoodName.setText("");
        binding.editAmount.setText("");
        binding.editDescription.setText("");
        binding.spinnerCategory.setText("", false);
        binding.layoutAmount.setError(null);
        selectedDateTime.setTime(new Date());
        updateDateDisplay();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
