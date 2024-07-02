package com.example.finalproject;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText username,password,email;
    TextView loginlink;
    Button signupbtn;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        loginlink = findViewById(R.id.loginlink);
        signupbtn = findViewById(R.id.signup);

        loginlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");
                String emailuser = email.getText().toString().trim();
                String userusername = username.getText().toString().trim();
                String passUser = password.getText().toString().trim();

                if (userusername.isEmpty()|| emailuser.isEmpty()
                        || passUser.isEmpty() ){
                    Toast.makeText(SignUpActivity.this, "Please fill all the fields",
                            Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Model user = new Model(userusername,emailuser,passUser);
                    reference.child(userusername).setValue(user);
                    Toast.makeText(SignUpActivity.this, "Signup successfully",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                    intent.putExtra("username",userusername);
                    intent.putExtra("email",emailuser);
                    intent.putExtra("password",passUser);

                    startActivity(intent);
                    finish();
                }

            }
        });

    }
}