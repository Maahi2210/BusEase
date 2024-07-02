package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginusername = findViewById(R.id.usernamelogin);
        loginpass = findViewById(R.id.passwordlogin);
        loginbtn = findViewById(R.id.login);
        forgetpass = findViewById(R.id.forgetpass);
        singuplink = findViewById(R.id.signuplink);

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


    public void checkuser(){
        String usernameLogin = loginusername.getText().toString().trim();
        String passLogin = loginpass.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("users");
        Query checkuserData = reference.orderByChild("username")
                .equalTo(usernameLogin);
        checkuserData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String passDB = snapshot.child(usernameLogin).child("password")
                            .getValue(String.class);
                    if (passDB.equals(passLogin)){
                        String usernameDB = snapshot.child(usernameLogin).child("username")
                                .getValue(String.class);
                        String passDBl = snapshot.child(usernameLogin).child("password")
                                .getValue(String.class);
                        String emailDB =snapshot.child(usernameLogin).child("email")
                                .getValue(String.class);
                        Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
                        intent.putExtra("email",emailDB);
                        intent.putExtra("password",passDBl);
                        intent.putExtra("username",usernameDB);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this, "Password incorrect", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}