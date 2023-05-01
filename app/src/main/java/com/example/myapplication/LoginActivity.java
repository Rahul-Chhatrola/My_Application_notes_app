package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText,passwordEdittext;
    Button loginBtn;
    ProgressBar progressBar;
    TextView CreateAccountBtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEdittext = findViewById(R.id.password_edit_text);
        progressBar = findViewById(R.id.progress_bar);
        loginBtn = findViewById(R.id.Login_btn);
        CreateAccountBtnTextView = findViewById(R.id.create_account_text_view_btn);

        loginBtn.setOnClickListener((v)-> loginUser());
        CreateAccountBtnTextView.setOnClickListener((v)-> startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class)));
    }

    void loginUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEdittext.getText().toString();

        boolean isValidated = validateData(email,password);
        if (!isValidated) {
            return;
        }

        loginAccountInFirebase(email,password);
    }

    void loginAccountInFirebase(String email,String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if (task.isSuccessful()) {
                    if (Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()) {
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    } else {
                        Utility.showToast(LoginActivity.this,"Email is not verified, please verify your email");
                    }
                } else {
                    Utility.showToast(LoginActivity.this,(Objects.requireNonNull(task.getException())).getLocalizedMessage());
                }
            }
        });

    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }
    boolean validateData(String email,String password) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email");
            return false;
        }
        if (password.length()<6) {
            passwordEdittext.setError("Password should be more than 6 characters");
            return false;
        }
        return true;
    }

}