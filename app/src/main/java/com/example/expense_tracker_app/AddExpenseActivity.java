package com.example.expense_tracker_app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expense_tracker_app.databinding.ActivityAddExpenseBinding;
import com.example.expense_tracker_app.models.Expense;
import com.example.expense_tracker_app.services.ExpenseService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    private ActivityAddExpenseBinding binding;
    private ExpenseService expenseService;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize View Binding
        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Service
        // CRITICAL: Ensure ExpenseService is initialized to prevent NullPointerException on save
        expenseService = new ExpenseService();

        setupCategorySpinner();
        setupDatePicker();

        // Handle "Add Expense" button click
        binding.btnAddFoodExpense.setOnClickListener(v -> saveExpense());
    }

    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.food_categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerFoodCategory.setAdapter(adapter);
    }

    private void setupDatePicker() {
        binding.inputFoodDate.setOnClickListener(v -> showDatePickerDialog());
        binding.inputLayoutDate.setOnClickListener(v -> showDatePickerDialog());
        updateDateField();
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateField();
        };

        new DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateField() {
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        binding.inputFoodDate.setText(sdf.format(calendar.getTime()));
    }

    // --- Core Logic: Saving the Expense ---

    private void saveExpense() {
        // 1. Collect all input data
        String name = binding.inputFoodName.getText().toString().trim(); // Item Name/Description
        String amountString = binding.inputFoodAmount.getText().toString().trim();
        String category = binding.spinnerFoodCategory.getSelectedItem().toString();
        String date = binding.inputFoodDate.getText().toString();

        // 2. Validation
        if (name.isEmpty() || amountString.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountString);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid amount.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Create the Model object (Expense)
        // ARGUMENT ORDER MUST BE: (category, amount, date, description)
        Expense newExpense = new Expense(
                category,    // First argument
                amount,
                date,
                name         // Fourth argument (the detailed item name)
        );

        // 4. Use the Service to persist the data
        expenseService.saveExpense(newExpense);

        // 5. Signal success back to MainActivity
        setResult(Activity.RESULT_OK);

        // 6. Close the current activity
        finish();
    }
}