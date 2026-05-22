package com.example.mathsprout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class QuizListActivity extends AppCompatActivity {

    Button easyBtn, mediumBtn, hardBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);

        easyBtn = findViewById(R.id.quizEasyBtn);
        mediumBtn = findViewById(R.id.quizMediumBtn);
        hardBtn = findViewById(R.id.quizHardBtn);

        View.OnClickListener quizClick = v -> {
            Intent intent = new Intent(QuizListActivity.this, QuizAttemptActivity.class);
            startActivity(intent);
        };

        easyBtn.setOnClickListener(quizClick);
        mediumBtn.setOnClickListener(quizClick);
        hardBtn.setOnClickListener(quizClick);
    }
}
