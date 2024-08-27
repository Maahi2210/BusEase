package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPasswordActivity";
    private EditText emailEditText;
    private Button resetPasswordButton;
    private FirebaseAuth mAuth;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.email);
        resetPasswordButton = findViewById(R.id.resetpassword);
        mAuth = FirebaseAuth.getInstance();
        backButton = findViewById(R.id.back);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();

                if (email.isEmpty()) {
                    emailEditText.setError("Email is required");
                    emailEditText.requestFocus();
                    return;
                }

                resetPassword(email);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Password reset email sent.");
                        Toast.makeText(ForgotPasswordActivity.this, "Check your email for the reset link", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Password reset email failed.", task.getException());
                        Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset email. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
