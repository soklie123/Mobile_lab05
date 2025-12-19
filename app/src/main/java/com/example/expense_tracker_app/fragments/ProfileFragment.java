package com.example.expense_tracker_app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.expense_tracker_app.LoginActivity;
import com.example.expense_tracker_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private TextView profileName, profileEmail;
    private Button btnLogout, btnUpdateProfile;

    // Use onCreateView to inflate the layout and find views
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout using the fragment's XML file
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        // Initialize UI elements from the inflated view
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnUpdateProfile = view.findViewById(R.id.btn_update_profile);

        // Load user data when the view is created
        loadUserProfile();

        // Logout button with confirmation dialog
        btnLogout.setOnClickListener(v -> showLogoutDialog());


        // Add logic for the Update Profile button
        btnUpdateProfile.setOnClickListener(v ->
            Toast.makeText(getContext(), "Opening Profile Edit Screen...", Toast.LENGTH_SHORT).show()
        );
        return view;
    }

    // Show logout confirmation dialog


    private void showLogoutDialog() {
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Ok", (dialog, which) -> signOutUser())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
    // Load current user profile info
    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String name = user.getDisplayName() != null && !user.getDisplayName().isEmpty()
                    ? user.getDisplayName() : "Anonymous User";

            profileName.setText(name);
            profileEmail.setText(user.getEmail());

        } else {
            // If the user is unexpectedly null, sign out and navigate back to login
            signOutUser();
        }
    }

    /**
     * Executes the Firebase sign-out command and navigates to the Login screen.
     */
    private void signOutUser() {
        // 1. Call the Firebase Sign Out method
        mAuth.signOut();
        Toast.makeText(getContext(), "Logged out successfully.", Toast.LENGTH_SHORT).show();

        // 2. Navigate back to the LoginActivity
        // We use requireActivity() to get the containing activity context for navigation
        Intent intent = new Intent(requireActivity(), LoginActivity.class);

        // CRITICAL: Use these flags to clear all previous activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // 3. Close the current activity
        requireActivity().finish();
    }
}