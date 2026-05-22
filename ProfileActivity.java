package com.example.mathsprout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText nameEt, phoneEt, passwordEt;
    private TextInputLayout phoneLayout, passwordLayout;
    private TextView roleTitle;
    private MaterialButton saveProfileBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference database;
    private String uid, role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameEt = findViewById(R.id.nameEt);
        phoneEt = findViewById(R.id.phoneEt);
        passwordEt = findViewById(R.id.passwordEt);
        roleTitle = findViewById(R.id.roleTitle);
        phoneLayout = findViewById(R.id.phoneLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        saveProfileBtn = findViewById(R.id.saveProfileBtn);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("Users");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
            loadUserProfile();
        }

        saveProfileBtn.setOnClickListener(v -> saveProfile());
        findViewById(R.id.logoutBtn).setOnClickListener(v -> logout());
    }

    private void loadUserProfile() {
        database.child(uid).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    nameEt.setText(user.getName());
                    role = user.getRole();

                    // Set the role as a simple subtitle in the header
                    roleTitle.setText(role != null ? role.toUpperCase() : "STUDENT");

                    if ("Educator".equalsIgnoreCase(role)) {
                        phoneLayout.setVisibility(View.VISIBLE);
                        passwordLayout.setVisibility(View.VISIBLE);
                        phoneEt.setText(user.getPhone());
                    }
                }
            }
        });
    }

    private void saveProfile() {
        String name = nameEt.getText().toString().trim();
        String phone = phoneEt.getText().toString().trim();
        String newPassword = passwordEt.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            nameEt.setError("Name required");
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Updating...");
        pd.show();

        DatabaseReference userRef = database.child(uid);
        userRef.child("name").setValue(name).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if ("Educator".equalsIgnoreCase(role)) {
                    userRef.child("phone").setValue(phone);
                    if (!TextUtils.isEmpty(newPassword)) {
                        mAuth.getCurrentUser().updatePassword(newPassword);
                    }
                }
                pd.dismiss();
                Snackbar.make(saveProfileBtn, "Profile updated 🌱", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        mAuth.signOut();
        finish();
    }
}