package com.achieve760.diy760;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;

    // Define the toggle buttons
    private ToggleButton emailToggle, passwordToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);

        emailEditText = findViewById(R.id.email_edittext);
        passwordEditText = findViewById(R.id.password_edittext);

        Button submitButton = findViewById(R.id.login_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });

        TextView guestLoginButton = findViewById(R.id.guest_login_textview);
        guestLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onRegisterClicked(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void submitForm() {
        boolean isValid = true;

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate email
        if (email.isEmpty()) {
            emailEditText.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(getString(R.string.error_invalid_email));
            isValid = false;
        } else {
            emailEditText.setError(null);
        }

        // Validate password
        if (password.isEmpty()) {
            passwordEditText.setError(getString(R.string.error_field_required));
            isValid = false;
        } else {
            passwordEditText.setError(null);
        }

        if (isValid) {
            // Authenticate user using Firebase Authentication
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // User authenticated successfully
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // optional: remove this if you want the user to be able to navigate back to the LoginActivity
                            } else {
                                // Authentication failed
                                Exception exception = task.getException();
                                if (exception != null) {
                                    Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
        }
    }
}
