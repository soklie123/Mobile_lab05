package com.example.expense_tracker_app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.expense_tracker_app.R;
import com.example.expense_tracker_app.models.Expense;
import com.example.expense_tracker_app.api.ExpenseApi;
import com.example.expense_tracker_app.utils.ExpenseRepository;
import com.example.expense_tracker_app.utils.LocaleHelper;
import com.example.expense_tracker_app.utils.ThemeHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {

    private ExpenseRepository expenseRepository;
    private Preference unsyncedPref;


    private static final String PREF_LANGUAGE = "app_language";
    private static final String PREF_THEME = "app_theme";
    private static final String PREF_BACKUP = "backup_expenses";

    private ExpenseApi expenseApi;


    // ActivityResultLauncher for the file creation intent
    private final ActivityResultLauncher<Intent> backupLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        writeBackupToUri(uri);
                    } else {
                        Toast.makeText(getContext(), "Failed to get file location.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);

        expenseApi = new ExpenseApi();
        unsyncedPref = findPreference("unsynced_tasks");

        expenseRepository = new ExpenseRepository(requireActivity().getApplication());

        expenseRepository
                .getAllExpensesLiveData()
                .observe(this, expenses -> {
                    if (unsyncedPref == null || expenses == null) return;

                    int unsyncedCount = 0;
                    for (Expense e : expenses) {
                        if (!e.isSynced()) unsyncedCount++;
                    }

                    unsyncedPref.setSummary(
                            unsyncedCount + " tasks pending sync"
                    );
                });


        if (unsyncedPref != null) {
            unsyncedPref.setOnPreferenceClickListener(preference -> {

                expenseRepository.syncExpenses(new ExpenseRepository.IExpenseCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(getContext(),
                                "All expenses synced successfully",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(getContext(),
                                errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }
                });

                return true;
            });
        }


        // Language preference
        ListPreference languagePref = findPreference(PREF_LANGUAGE);
        if (languagePref != null) {
            languagePref.setOnPreferenceChangeListener((preference, newValue) -> {
                LocaleHelper.setLocale(requireContext(), newValue.toString());
                requireActivity().recreate();
                return true;
            });
        }

        // Theme preference
        ListPreference themePref = findPreference(PREF_THEME);
        if (themePref != null) {
            themePref.setOnPreferenceChangeListener((preference, newValue) -> {
                String theme = newValue.toString();
                ThemeHelper.applyTheme(theme);
                ThemeHelper.persistTheme(requireContext(), theme);
                return true;
            });
        }

        // Backup preference
        Preference backupPref = findPreference("backup_expenses");
        if (backupPref != null) {
            backupPref.setOnPreferenceClickListener(preference -> {
                backupExpenses();
                return true;
            });
        }
    }

    private void backupExpenses() {
        createBackupFile();
    }

    private void createBackupFile() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "expenses_backup_" + timestamp + ".json";

        // Use Storage Access Framework to let the user choose where to save the file.
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);

        backupLauncher.launch(intent);
    }

    private void writeBackupToUri(Uri uri) {
        Toast.makeText(getContext(), getString(R.string.backup_in_progress), Toast.LENGTH_SHORT).show();

        expenseApi.getAllExpenses(new ExpenseApi.AllExpensesCallback() {
            @Override
            public void onSuccess(List<Expense> expenses) {
                if (getContext() == null) return;

                try {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(expenses);

                    try (OutputStream os = getContext().getContentResolver().openOutputStream(uri)) {
                        if (os != null) {
                            os.write(json.getBytes());
                            Toast.makeText(getContext(), "Backup saved successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            throw new IOException("Failed to open output stream.");
                        }
                    }
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Error saving backup: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Error fetching expenses: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Adjust for system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(getListView(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
    }
}
