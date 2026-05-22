package com.example.mathsprout;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LessonDetailActivity extends AppCompatActivity {

    private TextView lessonTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_detail);

        lessonTitle = findViewById(R.id.lessonTitle);

        String type = getIntent().getStringExtra("lessonType");
        lessonTitle.setText(type + " Lesson");
    }
}
