package com.example.mathsprout;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.zxing.BarcodeFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateClassActivity extends AppCompatActivity {

    private TextInputLayout classTitleLayout;
    private EditText classTitleEt;
    private Button createBtn;
    private TextView codeTv;
    private ImageView qrImage;
    private LinearLayout resultLayout; 

    private DatabaseReference classesRef, usersRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);

        
        classTitleLayout = findViewById(R.id.classTitleLayout);
        classTitleEt = findViewById(R.id.classTitleEt);
        createBtn = findViewById(R.id.createClassBtn);
        codeTv = findViewById(R.id.classCodeTv);
        qrImage = findViewById(R.id.classQrImage);
        resultLayout = findViewById(R.id.resultLayout);

        auth = FirebaseAuth.getInstance();
        classesRef = FirebaseDatabase.getInstance().getReference("Classes");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        createBtn.setOnClickListener(v -> createClass());
    }

    private void createClass() {
        String title = classTitleEt.getText().toString().trim();

        // Reset error
        classTitleLayout.setError(null);

        if (title.isEmpty()) {
            classTitleLayout.setError("Enter a class title to continue");
            return;
        }

      
        String classCode = "MS-" + UUID.randomUUID().toString().substring(0,6).toUpperCase();
        String educatorUid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "unknown";

        Map<String,Object> classObj = new HashMap<>();
        classObj.put("classId", classCode);
        classObj.put("title", title);
        classObj.put("educatorUid", educatorUid);
        classObj.put("timestamp", System.currentTimeMillis());

        createBtn.setEnabled(false); 

        classesRef.child(classCode).setValue(classObj)
                .addOnSuccessListener(aVoid -> {
                    codeTv.setText(classCode);
                    generateQr(classCode);

                
                    resultLayout.setVisibility(View.VISIBLE);
                    createBtn.setEnabled(true);
                    createBtn.setText("Class Created!");

                  
                    usersRef.child(educatorUid).child("classCode").setValue(classCode);

                    Toast.makeText(this, "Class successfully established!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    createBtn.setEnabled(true);
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void generateQr(String code) {
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(code, BarcodeFormat.QR_CODE, 400, 400);
            qrImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
