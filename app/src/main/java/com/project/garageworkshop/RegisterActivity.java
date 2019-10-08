package com.project.garageworkshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText email, passwd, name, surname, carNumber;
    final int role = 0;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        passwd = findViewById(R.id.passwd);
        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        carNumber = findViewById(R.id.carNumb);

        findViewById(R.id.customerReg).setOnClickListener(this);
    }

    void registerUser() {
        String emailString = email.getText().toString().trim();
        String passwdString = passwd.getText().toString().trim();
        if (TextUtils.isEmpty(emailString)){
            email.setError("Email address is required!");
            email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            email.setError("E-mail address is not correct! ");
            email.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(passwdString)){
            passwd.setError("Password is required!");
            passwd.requestFocus();
            return;
        }
        if (passwdString.length() < 6) {
            passwd.setError("The password length must be greater than 5 symbols!");
            passwd.requestFocus();
            return;
        }
        auth.createUserWithEmailAndPassword(emailString, passwdString).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String user_id = auth.getCurrentUser().getUid();
                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                    String nameString = name.getText().toString().trim();
                    String surnameString = surname.getText().toString().trim();
                    String carNumberString = carNumber.getText().toString().trim();
                    Map newPost = new HashMap<>();
                    newPost.put("role", role);
                    newPost.put("name", nameString);
                    newPost.put("surname", surnameString);
                    newPost.put("ID", carNumberString);
                    current_user_db.setValue(newPost);
                    Intent intent =new Intent(RegisterActivity.this, CarsListActivity.class);
                    startActivity(intent);
                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();


                }
                else {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "Account with this e-mail is already in use!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.customerReg:
                registerUser();
                break;
        }
    }
}

