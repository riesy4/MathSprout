package com.example.mathsprout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private TextInputLayout nameLayout, emailLayout, passwordLayout, confirmPasswordLayout;
    private EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signupButton;
    private ProgressBar progressBar;
    private TextView loginLink;
    private RadioGroup roleGroup;
    private FirebaseAuth mAuth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("Users");
        initViews();
        signupButton.setOnClickListener(v -> registerUser());

        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }
    private void initViews() {
        nameEditText = findViewById(R.id.signupName);
        emailEditText = findViewById(R.id.signupEmail);
        passwordEditText = findViewById(R.id.signupPassword);
        confirmPasswordEditText = findViewById(R.id.signupConfirmPassword);

        nameLayout = (TextInputLayout) nameEditText.getParent().getParent();
        emailLayout = (TextInputLayout) emailEditText.getParent().getParent();
        passwordLayout = (TextInputLayout) passwordEditText.getParent().getParent();
        confirmPasswordLayout = (TextInputLayout) confirmPasswordEditText.getParent().getParent();

        signupButton = findViewById(R.id.signupButton);
        progressBar = findViewById(R.id.signupProgress);
        loginLink = findViewById(R.id.loginLink);
        roleGroup = findViewById(R.id.roleGroup);
    }

    private void registerUser() {
        nameLayout.setError(null);
        emailLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);

        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            nameLayout.setError("Name is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Email is required");
            return;
        }
        if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters");
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Passwords do not match");
            confirmPasswordEditText.requestFocus();
            return;
        }

        int selectedRoleId = roleGroup.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(this, "Please select if you are an Educator or Child",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRoleButton = findViewById(selectedRoleId);
        String role = selectedRoleButton.getText().toString();

        progressBar.setVisibility(View.VISIBLE);
        signupButton.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                        saveUserToDatabase(name, email, role);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        signupButton.setEnabled(true);
                        Toast.makeText(SignupActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void saveUserToDatabase(String name, String email, String role) {
        String uid = mAuth.getCurrentUser().getUid();
        long createdAt = System.currentTimeMillis();

        User newUser = new User(name, email, role, uid, createdAt);

        database.child(uid).setValue(newUser)
                .addOnCompleteListener(saveTask -> {
                    progressBar.setVisibility(View.GONE);
                    if (saveTask.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, "Welcome to MathSprout!", Toast.LENGTH_SHORT).show();

                        if ("Child".equalsIgnoreCase(role)) {
                            startActivity(new Intent(SignupActivity.this, LinkTeacherActivity.class));
                        } else {
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        }
                        finish();
                    } else {
                        signupButton.setEnabled(true);
                        Toast.makeText(SignupActivity.this, "Failed to save profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}