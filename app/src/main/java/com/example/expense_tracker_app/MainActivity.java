package com.example.expense_tracker_app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BaseActivity {

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
        // It automatically handles fragment switching, back stack, and correct item selection.
        NavigationUI.setupWithNavController(bottomNavigation, navController);
    }
}
