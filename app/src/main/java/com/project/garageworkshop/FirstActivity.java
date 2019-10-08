package com.project.garageworkshop;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth auth;
    EditText email, passwd;
    DatabaseReference databaseReference;
    Button logInButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstactivitydesign);
        FirebaseApp.initializeApp(this);
        auth =FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        passwd = findViewById(R.id.passwd);
        findViewById(R.id.registerButton).setOnClickListener(this);
        findViewById(R.id.logInButton).setOnClickListener(this);
    }
    private void userLogin() {
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
        auth.signInWithEmailAndPassword(emailString, passwdString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    logInButton = findViewById(R.id.logInButton);
                    readRole(new OnGetDataListener() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(DataSnapshot data) {
                            if (data.child("role").exists()) {
                                int role = data.child("role").getValue(int.class);
                                if (role == 0) {
                                    Intent intent = new Intent(FirstActivity.this, CarsListActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    Toast.makeText(FirstActivity.this, "No other role yet", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(FirstActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailed(DatabaseError databaseError) {
                            Toast.makeText(FirstActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    interface OnGetDataListener {
        void onStart();
        void onSuccess(DataSnapshot data);
        void onFailed(DatabaseError databaseError);

    }

    void openRegistrationActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    void readRole(final OnGetDataListener listener) {
        final String user_id = auth.getInstance().getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        listener.onStart();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }

        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.registerButton:
                openRegistrationActivity();
                break;
            case R.id.logInButton:
                userLogin();
                break;
        }
    }
}
