package com.example.mathsprout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText loginEmail, loginPassword;
    private MaterialButton loginButton;
    private ProgressBar loginProgress;
    private TextView signupLink, forgotPasswordText;

    private FirebaseAuth mAuth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);

        loginEmail = findViewById(R.id.emailEditText);
        loginPassword = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        loginProgress = findViewById(R.id.progressBar);
        signupLink = findViewById(R.id.signupText);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("Users");

        loginButton.setOnClickListener(v -> loginUser());

        signupLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });

        forgotPasswordText.setOnClickListener(v -> showResetPasswordDialog());
    }

    private void loginUser() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            loginEmail.setError("Email is required");
            loginEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            loginPassword.setError("Password is required");
            loginPassword.requestFocus();
            return;
        }

        loginProgress.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    loginProgress.setVisibility(View.GONE);
                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                        String uid = mAuth.getCurrentUser().getUid();
                        database.child(uid).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful() && task1.getResult().exists()) {
                                User user = task1.getResult().getValue(User.class);
                                if (user != null && user.role != null) {
                                    SessionManager session = new SessionManager(this);
                                    if (!user.role.equalsIgnoreCase("Educator")) {
                                        session.saveChildId(uid);
                                    }
                                    navigateByRole(user.role.trim());
                                } else {
                                    Toast.makeText(this, "User role not found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Login failed: " +
                                        (task.getException() != null ? task.getException().getMessage() : "Check credentials"),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void navigateByRole(String role) {
        Intent intent;
        if (role.equalsIgnoreCase("Educator")) {
            intent = new Intent(this, EducatorMainActivity.class);
        } else {
            intent = new Intent(this, ChildMainActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void showResetPasswordDialog() {
        final EditText resetEmail = new EditText(this);
        resetEmail.setHint("Enter your email");

        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.leftMargin = 50; params.rightMargin = 50;
        resetEmail.setLayoutParams(params);
        container.addView(resetEmail);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setMessage("Enter your email to receive a reset link")
                .setView(container)
                .setPositiveButton("Send", (dialog, which) -> {
                    String email = resetEmail.getText().toString().trim();
                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(LoginActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    sendPasswordResetEmail(email);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sendPasswordResetEmail(String email) {
        loginProgress.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    loginProgress.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this,
                                "Reset link sent to " + email,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Error: " + (task.getException() != null ?
                                        task.getException().getMessage() : "Failed"),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
