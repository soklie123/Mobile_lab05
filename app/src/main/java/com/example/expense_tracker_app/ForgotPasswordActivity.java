package com.example.expense_tracker_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

        // Connect input
        emailEditText = findViewById(R.id.edit_text_reset_email);

        // Reset Button
        Button resetButton = findViewById(R.id.button_reset_password);
        resetButton.setOnClickListener(v -> sendResetEmail());


        // Back to login
        TextView backToLogin = findViewById(R.id.text_back_to_login);
        backToLogin.setOnClickListener(v -> openActivity(LoginActivity.class));

        // System bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupClickListeners();
    }

    private void sendResetEmail() {
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Reset email sent! Check your inbox.", Toast.LENGTH_LONG).show();
                        openLogin();
                    } else {
                        Toast.makeText(this,
                                "Error:" + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                });
    }

    private void openLogin() {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void setupClickListeners() {
        findViewById(R.id.text_back_to_login).setOnClickListener(v ->
                openActivity(LoginActivity.class));
    }
    private void openActivity(Class<?> cls) {
        Intent intent = new Intent(ForgotPasswordActivity.this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}