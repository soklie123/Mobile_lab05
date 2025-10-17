package com.example.expense_tracker_app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.expense_tracker_app.fragments.AddExpenseFragment;
import com.example.expense_tracker_app.fragments.HomeFragment;
import com.example.expense_tracker_app.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private Fragment homeFragment;
    private Fragment addExpenseFragment;
    private Fragment profileFragment;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeFragment = new HomeFragment();
        addExpenseFragment = new AddExpenseFragment();
        profileFragment = new ProfileFragment();
        activeFragment = homeFragment;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_container, profileFragment, "profile").hide(profileFragment);
        ft.add(R.id.fragment_container, addExpenseFragment, "add").hide(addExpenseFragment);
        ft.add(R.id.fragment_container, homeFragment, "home"); // show home by default
        ft.commit();

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home_fragment) {
                switchFragment(homeFragment);
            } else if (id == R.id.add_expense_fragment) {
                switchFragment(addExpenseFragment);
            } else if (id == R.id.profile_fragment) {
                switchFragment(profileFragment);
            } else {
                return false;
            }
            return true;
        });


    }

    private void switchFragment(Fragment targetFragment) {
        if (activeFragment == targetFragment) return;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(activeFragment);
        ft.show(targetFragment);
        ft.commit();

        activeFragment = targetFragment;
    }
}
