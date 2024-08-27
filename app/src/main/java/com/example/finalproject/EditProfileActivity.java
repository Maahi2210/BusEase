package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.concurrent.CountDownLatch;

public class EditProfileActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText;
    private Button saveButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        saveButton = findViewById(R.id.saveButton);

        if (firebaseUser != null) {
            emailEditText.setText(firebaseUser.getEmail());
            userDatabaseReference.child(firebaseUser.getUid()).child("username").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String username = task.getResult().getValue(String.class);
                    usernameEditText.setText(username);
                }
            });
        }

        saveButton.setOnClickListener(v -> updateProfile());
    }

    private void updateProfile() {
        String newUsername = usernameEditText.getText().toString().trim();
        String newEmail = emailEditText.getText().toString().trim();
        String newPassword = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(newEmail)) {
            emailEditText.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            passwordEditText.setError("Password is required");
            return;
        }

        if (firebaseUser != null) {
            final CountDownLatch latch = new CountDownLatch(3);
            final boolean[] success = {true};

            firebaseUser.updateEmail(newEmail).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    success[0] = false;
                    Toast.makeText(EditProfileActivity.this, "Email update failed", Toast.LENGTH_SHORT).show();
                }
                latch.countDown();
            });

            firebaseUser.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    success[0] = false;
                    Toast.makeText(EditProfileActivity.this, "Password update failed", Toast.LENGTH_SHORT).show();
                }
                latch.countDown();
            });

            userDatabaseReference.child(firebaseUser.getUid()).child("username").setValue(newUsername).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    success[0] = false;
                    Toast.makeText(EditProfileActivity.this, "Username update failed", Toast.LENGTH_SHORT).show();
                }
                latch.countDown();
            });

            new Thread(() -> {
                try {
                    latch.await();
                    runOnUiThread(() -> {
                        if (success[0]) {
                            Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditProfileActivity.this, Weather_Activity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
