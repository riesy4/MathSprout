package com.example.mathsprout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class InfoActivity extends AppCompatActivity {
    private MaterialButton getStartedBtn;
    private ImageView appIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        getStartedBtn = findViewById(R.id.getStartedButton);
        appIcon = findViewById(R.id.appIcon);

        if (appIcon != null) {
            appIcon.setImageResource(R.drawable.app_icon);
        }

        if (getStartedBtn != null) {
            getStartedBtn.setOnClickListener(v -> {
                Intent intent = new Intent(InfoActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }
}