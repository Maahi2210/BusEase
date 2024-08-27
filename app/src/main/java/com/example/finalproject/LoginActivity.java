package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText loginusername,loginpass;
    Button loginbtn,forgetpass;
    TextView singuplink;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginusername = findViewById(R.id.usernamelogin);
        loginpass = findViewById(R.id.passwordlogin);
        loginbtn = findViewById(R.id.login);
        forgetpass = findViewById(R.id.forgetpass);
        singuplink = findViewById(R.id.signuplink);
        mAuth = FirebaseAuth.getInstance();
        singuplink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkuser();
            }
        });
        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    public void checkUser() {
        Log.d("login activity", "In side password");
        String usernameLogin = loginusername.getText().toString().trim();
        String passLogin = loginpass.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserData = reference.orderByChild("username").equalTo(usernameLogin);
        Log.d("login activity", "Query path: " + checkUserData.toString());

        checkUserData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("login activity", "DataSnapshot: " + snapshot.toString());
                if (snapshot.exists()) {
                    Log.d("login activity", "In side password");
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                    String passDB = userSnapshot.child("password").getValue(String.class);
                    if (passDB != null && passDB.equals(passLogin)) {
                        Log.d("login activity", "In side password");
                        String usernameDB = userSnapshot.child("username").getValue(String.class);
                        String emailDB = userSnapshot.child("email").getValue(String.class);
                        Intent intent = new Intent(LoginActivity.this, Weather_Activity.class);
                        intent.putExtra("email", emailDB);
                        intent.putExtra("username", usernameDB);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Password incorrect", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("login activity", "Database error: " + error.getMessage());
            }
        });
    }
public void checkuser()
{
    String email = loginusername.getText().toString().trim();
    String password = loginpass.getText().toString().trim();
    mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this,"success ",Toast.LENGTH_SHORT).show();

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {

                            Intent intent = new Intent(LoginActivity.this, Weather_Activity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {

                        Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }