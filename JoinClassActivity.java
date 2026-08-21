package com.example.mathsprout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class JoinClassActivity extends AppCompatActivity {

    private EditText codeEt;
    private Button joinBtn, scanQrBtn;
    private ProgressBar progressBar;

    private DatabaseReference classesRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_class);

        codeEt = findViewById(R.id.classCodeEt);
        joinBtn = findViewById(R.id.joinClassBtn);
        scanQrBtn = findViewById(R.id.scanQrBtn);
        progressBar = findViewById(R.id.joinClassProgress);

        auth = FirebaseAuth.getInstance();
        classesRef = FirebaseDatabase.getInstance().getReference("Classes");

        joinBtn.setOnClickListener(v -> joinClass());
        scanQrBtn.setOnClickListener(v -> {
            new com.google.zxing.integration.android.IntentIntegrator(this)
                    .setPrompt("Scan Class QR Code")
                    .setBeepEnabled(true)
                    .setOrientationLocked(false)
                    .initiateScan();
        });
    }

    private void joinClass() {
        String code = codeEt.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            codeEt.setError("Enter class code");
            return;
        }

        progressBar.setVisibility(android.view.View.VISIBLE);

        classesRef.child(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(android.view.View.GONE);
                if (snapshot.exists()) {
                    String uid = auth.getCurrentUser().getUid();
                    classesRef.child(code).child("members").child(uid).setValue(true);

                    // Save classCode in user profile
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(uid).child("classCode").setValue(code);

                    Toast.makeText(JoinClassActivity.this, "Joined class " + code, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(JoinClassActivity.this, ChildMainActivity.class));
                    finish();
                } else {
                    Toast.makeText(JoinClassActivity.this, "Invalid class code", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(android.view.View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        com.google.zxing.integration.android.IntentResult result =
                com.google.zxing.integration.android.IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            codeEt.setText(result.getContents());
            joinClass();
        }
    }
}
