package com.example.expense_tracker_app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private static final int NOTIFICATION_PERMISSION_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the NavHostFragment which manages all fragment transactions
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        // Get the NavController from the NavHostFragment
        NavController navController = navHostFragment.getNavController();

        // Find the BottomNavigationView
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        // This single line of code connects the BottomNavigationView to the NavController.
        NavigationUI.setupWithNavController(bottomNavigation, navController);

        // Initialize FCM for push notifications
        initializeFirebaseMessaging();
    }

    /**
     * Initialize Firebase Cloud Messaging and get FCM token
     */
    private void initializeFirebaseMessaging() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM token failed", task.getException());
                        return;
                    }

                    // Get FCM token
                    String token = task.getResult();

                    // Log the token for testing
                    Log.i(TAG, "============================================");
                    Log.i(TAG, "FCM Token: " + token);
                    Log.i(TAG, "============================================");
                    Log.i(TAG, "Copy this token to test push notifications");
                    Log.i(TAG, "============================================");
                });
    }
}