package com.example.expense_tracker_app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class ThemeHelper {
    public static final String THEME_LIGHT_VALUE = "light";
    public static final String THEME_DARK_VALUE = "dark";
    public static final String THEME_DEFAULT_VALUE = "default"; // For "Follow System"

    private static final String PREF_THEME = "app_theme";

    /**
     * Applies the selected theme by changing the app's night mode.
     * This method is static and can be called from anywhere without creating an object.
     * @param theme The theme to apply ("light", "dark", or "default").
     */
    public static void applyTheme(String theme) {
        switch (theme) {
            case THEME_LIGHT_VALUE:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK_VALUE:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                // Follow the system setting by default
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    /**
     * Persists (saves) the user's theme choice in SharedPreferences.
     * This method is static and can be called from anywhere.
     * @param context The context needed to access SharedPreferences.
     * @param theme   The theme string to save (e.g., "dark").
     */
    public static void persistTheme(Context context, String theme) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_THEME, theme);
        editor.apply();
    }

    /**
     * Retrieves the saved theme preference from SharedPreferences.
     * @param context The context needed to access SharedPreferences.
     * @return The saved theme string, or a default value if none is saved.
     */
    public static String getThemeFromPreferences(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        // It's better to default to the system setting.
        return pref.getString(PREF_THEME, THEME_DEFAULT_VALUE);
    }
}
