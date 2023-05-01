package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {

    EditText emailEditText,passwordEdittext,confirmPasswordEditText;
    Button createAccountBtn;
    ProgressBar progressBar;
    TextView loginBtnTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEdittext = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        progressBar = findViewById(R.id.progress_bar);
        createAccountBtn = findViewById(R.id.create_account_btn);
        loginBtnTextView = findViewById(R.id.login_text_view_btn);

        createAccountBtn.setOnClickListener((v)-> createAccount());
        loginBtnTextView.setOnClickListener((v)-> finish());
    }

    void createAccount () {
        String email = emailEditText.getText().toString();
        String password = passwordEdittext.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        boolean isValidated = validateData(email,password,confirmPassword);
        if (!isValidated) {
            return;
        }

        createAccountBtnInFirebase(email,password);

    }

    void createAccountBtnInFirebase(String email, String password) {
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if (task.isSuccessful()) {
                            Utility.showToast(CreateAccountActivity.this,"Successfully create account,check email to verify");
                            Objects.requireNonNull(firebaseAuth.getCurrentUser()).sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        } else {
                            Utility.showToast(CreateAccountActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage());
                        }
                    }
                }
        );
    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }
    boolean validateData(String email,String password,String confirmPassword) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email");
            return false;
        }
        if (password.length()<5) {
            passwordEdittext.setError("Password should be more than 6 characters");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Password doesn't match");
            return false;
        }
        return true;
    }
}
