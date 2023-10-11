package com.achieve760.diy760;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout firstNameLayout;
    private TextInputEditText firstNameEditText;
    private TextInputLayout emailLayout;
    private TextInputEditText emailEditText;
    private TextInputLayout confirmEmailLayout;
    private TextInputEditText confirmEmailEditText;
    private TextInputLayout passwordLayout;
    private TextInputEditText passwordEditText;
    private TextInputLayout confirmPasswordLayout;
    private TextInputEditText confirmPasswordEditText;

    private CheckBox agreeCheckbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseApp.initializeApp(this);

        firstNameLayout = findViewById(R.id.firstname_layout);
        firstNameEditText = findViewById(R.id.firstname_edittext);
        emailLayout = findViewById(R.id.email_layout);
        emailEditText = findViewById(R.id.email_edittext);
        confirmEmailLayout = findViewById(R.id.confirm_email_layout);
        confirmEmailEditText = findViewById(R.id.confirm_email_edittext);
        passwordLayout = findViewById(R.id.password_layout);
        passwordEditText = findViewById(R.id.password_edittext);
        confirmPasswordLayout = findViewById(R.id.confirm_password_layout);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edittext);

        agreeCheckbox = findViewById(R.id.agree_checkbox);

        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });

        TextView loginTextView = findViewById(R.id.login_textview);
        loginTextView.setMovementMethod(LinkMovementMethod.getInstance());
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        TextView guestLoginButton = findViewById(R.id.guest_login_textview);
        guestLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void submitForm() {
        boolean isValid = true;

        // Get the input values
        String firstName = firstNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String confirmEmail = confirmEmailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // Validate first name
        if (firstName.isEmpty()) {
            firstNameLayout.setError(getString(R.string.error_field_required));
            isValid = false;
        } else {
            firstNameLayout.setError(null);
        }

        // Validate email
        if (email.isEmpty()) {
            emailLayout.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError(getString(R.string.error_invalid_email));
            isValid = false;
        } else {
            emailLayout.setError(null);
        }

        // Validate confirm email
        if (confirmEmail.isEmpty()) {
            confirmEmailLayout.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if (!confirmEmail.equals(email)) {
            confirmEmailLayout.setError(getString(R.string.error_confirm_email_mismatch));
            isValid = false;
        } else {
            confirmEmailLayout.setError(null);
        }

        // Validate password
        if (password.isEmpty()) {
            passwordLayout.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if (password.length() < 8) {
            passwordLayout.setError(getString(R.string.error_password_length));
            isValid = false;
        } else if (!password.matches(".*\\d.*")) {
            passwordLayout.setError(getString(R.string.error_password_digits));
            isValid = false;
        } else if (!password.matches(".*\\W.*")) {
            passwordLayout.setError(getString(R.string.error_password_symbols));
            isValid = false;
        } else {
            passwordLayout.setError(null);
        }

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            confirmPasswordLayout.setError(getString(R.string.error_field_required));
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            confirmPasswordLayout.setError(getString(R.string.error_confirm_password_mismatch));
            isValid = false;
        } else {
            confirmPasswordLayout.setError(null);
        }

        // Validate checkbox
        if (!agreeCheckbox.isChecked()) {
            Toast.makeText(this, "You must agree to the terms and conditions", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (isValid) {
            // Submit registration form and go to MainActivity
            // Create a new user account using Firebase Authentication
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // User account created successfully
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    // Save the user's first name to the database
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("firstName", firstName);
                                    databaseReference.child("users").child(user.getUid()).setValue(userData)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Data written successfully
                                                        // Go to MainActivity
                                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finish(); // optional: remove this if you want the user to be able to navigate back to the RegisterActivity
                                                    } else {
                                                        // Error writing data
                                                        Exception exception = task.getException();
                                                        if (exception != null) {
                                                            Toast.makeText(RegisterActivity.this, "Failed to write user data: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                }
                                            });
                                }

                            }
                        }
                    });
        }
    }

}