package com.example.mathsprout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class JoinWhiteboardActivity extends AppCompatActivity {
    private TextInputEditText sessionCodeInput;
    private Button joinBtn, scanQrBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_whiteboard);
        sessionCodeInput = findViewById(R.id.sessionCodeInput);
        joinBtn = findViewById(R.id.joinBtn);
        scanQrBtn = findViewById(R.id.scanQrBtn);

        sessionCodeInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String result = s.toString();
                if (!result.equals(result.toUpperCase())) {
                    sessionCodeInput.setText(result.toUpperCase());
                    sessionCodeInput.setSelection(sessionCodeInput.getText().length());
                }
            }
        });

        joinBtn.setOnClickListener(v -> {
            String code = sessionCodeInput.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(this, "Enter a code first!", Toast.LENGTH_SHORT).show();
                return;
            }
            goToChildWhiteboard(code);
        });

        scanQrBtn.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setPrompt("Align the Teacher's QR code inside the box");
            integrator.setBeepEnabled(true);
            integrator.setOrientationLocked(true);
            integrator.initiateScan();
        });
    }

    private void goToChildWhiteboard(String code) {
        Intent intent = new Intent(this, ChildWhiteboardActivity.class);
        intent.putExtra("SESSION_CODE", code);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                goToChildWhiteboard(result.getContents());
            }
        }
    }
}