package com.example.mathsprout;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LessonQuestionManagerActivity extends AppCompatActivity {

    private EditText questionInput, imageUrlInput;
    private Button addQuestionBtn, finishBtn;
    private TextView categoryText;

    private String category;
    private String educatorId;
    private List<Child> selectedChildren;

    private DatabaseReference lessonRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_question_manager);

        category = getIntent().getStringExtra("CATEGORY");
        educatorId = getIntent().getStringExtra("EDUCATOR_ID");
        selectedChildren = getIntent().getParcelableArrayListExtra("SELECTED_CHILDREN");

        lessonRef = FirebaseDatabase.getInstance().getReference("Lessons");

        initViews();
        setupListeners();
    }

    private void initViews() {
        categoryText = findViewById(R.id.categoryText);
        questionInput = findViewById(R.id.questionInput);
        imageUrlInput = findViewById(R.id.imageUrlInput);
        addQuestionBtn = findViewById(R.id.addQuestionBtn);
        finishBtn = findViewById(R.id.finishLessonBtn);

        categoryText.setText("Category: " + category.toUpperCase());
    }

    private void setupListeners() {
        addQuestionBtn.setOnClickListener(v -> addQuestion());
        finishBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Lesson assigned successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void addQuestion() {
        String questionText = questionInput.getText().toString().trim();
        String imageUrl = imageUrlInput.getText().toString().trim();

        if (TextUtils.isEmpty(questionText)) {
            questionInput.setError("Question is required");
            return;
        }

        Map<String, Object> questionData = new HashMap<>();
        questionData.put("question", questionText);
        questionData.put("imageUrl", imageUrl);
        questionData.put("category", category);
        questionData.put("educatorId", educatorId);
        questionData.put("timestamp", System.currentTimeMillis());

        for (Child child : selectedChildren) {
            lessonRef.child(child.getUid())
                    .child(category)
                    .push()
                    .setValue(questionData);
        }

        questionInput.setText("");
        imageUrlInput.setText("");
        Toast.makeText(this, "Question added", Toast.LENGTH_SHORT).show();
    }
}
