package com.example.expense_tracker_app;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expense_tracker_app.utils.LocaleHelper;
import com.example.expense_tracker_app.utils.ThemeHelper;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        String language = LocaleHelper.getLanguageFromPreferences(newBase);
        Context context = LocaleHelper.setLocale(newBase, language);
        super.attachBaseContext(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String theme = ThemeHelper.getThemeFromPreferences(this);
        ThemeHelper.applyTheme(theme);
        super.onCreate(savedInstanceState);
    }
}

