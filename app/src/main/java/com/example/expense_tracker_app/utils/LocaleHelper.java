package com.example.expense_tracker_app.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
// Correct import for PreferenceManager
import androidx.preference.PreferenceManager;

import java.util.Locale;

public class LocaleHelper {

    private static final String PREF_LANGUAGE = "app_language";

    public static Context setLocale(Context context, String language) {
        persist(context, language);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }

        return updateResourcesLegacy(context, language);
    }

    private static void persist(Context context, String language) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_LANGUAGE, language);
        editor.apply();
    }
    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        return context.createConfigurationContext(config);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = res.getConfiguration();
        config.locale = locale;
        config.setLayoutDirection(locale);

        res.updateConfiguration(config, res.getDisplayMetrics());

        return context;
    }

    // --- FIXES APPLIED TO THIS METHOD ---
    public static String getLanguageFromPreferences(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        // 1. Use the correct preference key for language
        // 2. Provide a sensible default value for language (e.g., "en" for English)
        return pref.getString(PREF_LANGUAGE, "en");
    }
}
