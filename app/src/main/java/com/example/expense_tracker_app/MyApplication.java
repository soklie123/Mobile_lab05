package com.example.expense_tracker_app;

import android.app.Application;
import android.content.Context;

import com.example.expense_tracker_app.utils.LocaleHelper;

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        String lang = LocaleHelper.getLanguageFromPreferences(base);
        super.attachBaseContext(LocaleHelper.setLocale(base, lang));
    }
}
