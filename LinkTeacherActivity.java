package com.example.mathsprout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class LinkTeacherActivity extends AppCompatActivity {

    private EditText teacherCodeEt;
    private Button linkBtn, scanQrBtn;
    private ProgressBar loader;

    private DatabaseReference teacherCodesRef, usersRef;
    private String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_teacher);

        teacherCodeEt = findViewById(R.id.teacherCodeEt);
        linkBtn = findViewById(R.id.linkBtn);
        scanQrBtn = findViewById(R.id.scanQrBtn);
        loader = findViewById(R.id.loader);
        myUid = FirebaseAuth.getInstance().getUid();
        teacherCodesRef = FirebaseDatabase.getInstance().getReference("TeacherCodes");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        linkBtn.setOnClickListener(v -> {
            String code = teacherCodeEt.getText().toString().trim().toUpperCase();
            if (!TextUtils.isEmpty(code)) {
                processLink(code);
            } else {
                Toast.makeText(this, "Please enter your teacher's code", Toast.LENGTH_SHORT).show();
            }
        });

        scanQrBtn.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setPrompt("Scan your Teacher's QR Code");
            integrator.setBeepEnabled(true);
            integrator.setOrientationLocked(true);
            integrator.initiateScan();
        });
    }

    private void processLink(String code) {
        loader.setVisibility(View.VISIBLE);
        linkBtn.setEnabled(false);

        teacherCodesRef.child(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String teacherUid = snapshot.getValue(String.class);

                    // Link the teacher's UID to this child's profile
                    usersRef.child(myUid).child("linkedTeacher").setValue(teacherUid)
                            .addOnCompleteListener(task -> {
                                loader.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(LinkTeacherActivity.this, "Successfully linked!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(LinkTeacherActivity.this, ChildMainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                } else {
                    loader.setVisibility(View.GONE);
                    linkBtn.setEnabled(true);
                    Toast.makeText(LinkTeacherActivity.this, "Invalid Code! Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loader.setVisibility(View.GONE);
                linkBtn.setEnabled(true);
                Toast.makeText(LinkTeacherActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            processLink(result.getContents());
        }
    }
}