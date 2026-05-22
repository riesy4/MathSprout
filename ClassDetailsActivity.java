package com.example.mathsprout;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.zxing.BarcodeFormat;

import java.util.ArrayList;
import java.util.List;

public class ClassDetailsActivity extends AppCompatActivity {

    private TextView titleTv, codeTv;
    private ImageView qrImage;
    private ListView membersList;

    private DatabaseReference classesRef, usersRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        titleTv = findViewById(R.id.classTitleTv);
        codeTv = findViewById(R.id.classCodeTv);
        qrImage = findViewById(R.id.classQrImage);
        membersList = findViewById(R.id.membersList);

        auth = FirebaseAuth.getInstance();
        classesRef = FirebaseDatabase.getInstance().getReference("Classes");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        loadClassCode();
    }

    private void loadClassCode() {
        String uid = auth.getCurrentUser().getUid();
        usersRef.child(uid).child("classCode").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String classCode = snapshot.getValue(String.class);
                    loadClassDetails(classCode);
                } else {
                    Toast.makeText(ClassDetailsActivity.this, "No class found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadClassDetails(String classCode) {
        classesRef.child(classCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                String title = snapshot.child("title").getValue(String.class);
                titleTv.setText(title != null ? title : "Class");
                codeTv.setText("Class Code: " + classCode);

                generateQr(classCode);

                List<String> members = new ArrayList<>();
                for (DataSnapshot ds : snapshot.child("members").getChildren()) {
                    members.add(ds.getKey());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ClassDetailsActivity.this,
                        android.R.layout.simple_list_item_1, members);
                membersList.setAdapter(adapter);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void generateQr(String code) {
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(code, BarcodeFormat.QR_CODE, 300, 300);
            qrImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}