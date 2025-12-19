package com.example.expense_tracker_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends BaseActivity {

    private static final String TAG = "RegisterActivity";

    private FirebaseAuth mAuth;
    private Button registerButton;
    private EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Get TextInputLayouts from XML
        TextInputLayout emailLayout = findViewById(R.id.input_email);
        TextInputLayout passwordLayout = findViewById(R.id.input_password);

        // Get EditTexts inside TextInputLayouts
        emailEditText = emailLayout.getEditText();
        passwordEditText = passwordLayout.getEditText();

        // Get Register Button
        registerButton = findViewById(R.id.button_register);

        // Safety check
        if (emailEditText == null || passwordEditText == null || registerButton == null) {
            Log.e(TAG, "Some views are missing in XML!");
            Toast.makeText(this, "UI setup error. Please check your layout.", Toast.LENGTH_LONG).show();
            return;
        }

        // Register button click
        registerButton.setOnClickListener(v -> registerUser());

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            if (v != null) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            }
            return insets;
        });

        // Login text click
        if (findViewById(R.id.text_login_prompt) != null) {
            findViewById(R.id.text_login_prompt).setOnClickListener(v -> openActivity(LoginActivity.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void registerUser() {
        String email = emailEditText.getText() != null ? emailEditText.getText().toString().trim() : "";
        String password = passwordEditText.getText() != null ? passwordEditText.getText().toString().trim() : "";

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and Password are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                        updateUI(mAuth.getCurrentUser());
                    } else {
                        Toast.makeText(this, "Registration Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Firebase Registration Failed", task.getException());
                    }
                });
    }

    private void openActivity(Class<?> cls) {
        Intent intent = new Intent(RegisterActivity.this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            openActivity(MainActivity.class);
            finish();
        }
    }
}
