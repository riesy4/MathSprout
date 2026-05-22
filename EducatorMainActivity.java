package com.example.mathsprout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class EducatorMainActivity extends AppCompatActivity {

    private TextView welcomeText, classCodeTv;
    private ImageView classQrImage;
    private CardView manageLessonsBtn, viewStudentsBtn, liveWhiteboardBtn, viewPerformanceBtn, profileBtn, logoutBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef, teacherCodesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_educator_main);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        teacherCodesRef = FirebaseDatabase.getInstance().getReference("TeacherCodes");

        welcomeText = findViewById(R.id.welcomeText);
        classCodeTv = findViewById(R.id.classCodeTv);
        classQrImage = findViewById(R.id.classQrImage);

        manageLessonsBtn = findViewById(R.id.manageLessonsBtn);
        viewStudentsBtn = findViewById(R.id.viewStudentsBtn);
        liveWhiteboardBtn = findViewById(R.id.liveWhiteboardBtn);
        viewPerformanceBtn = findViewById(R.id.viewPerformanceBtn);
        profileBtn = findViewById(R.id.profileBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        setupTeacherDashboard();
        setupClickListeners();
    }

    private void setupTeacherDashboard() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            String teacherCode = "TCH-" + uid.substring(0, 6).toUpperCase();
            classCodeTv.setText("Teacher Code: " + teacherCode);

            usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        welcomeText.setText("Welcome, " + (name != null ? name : "Educator") + "!");
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {}
            });

            teacherCodesRef.child(teacherCode).setValue(uid);
            generateQrCode(teacherCode);
        }
    }

    private void generateQrCode(String code) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(code, BarcodeFormat.QR_CODE, 400, 400);
            classQrImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        manageLessonsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, ManageLessonsActivity.class)));

        viewStudentsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, ViewStudentsActivity.class)));

        liveWhiteboardBtn.setOnClickListener(v ->
                startActivity(new Intent(this, EducatorLiveWhiteboardActivity.class)));

        viewPerformanceBtn.setOnClickListener(v ->
                startActivity(new Intent(this, EducatorPerformanceActivity.class)));

        profileBtn.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}