package com.example.mathsprout;

import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LessonAnswerActivity extends AppCompatActivity {

    private TextView questionText, progressText;
    private ImageView questionImage;
    private RadioGroup optionsGroup;
    private RadioButton optA, optB, optC;
    private Button nextBtn;

    private DatabaseReference lessonRef;

    private List<Map<String, Object>> questions = new ArrayList<>();
    private List<Map<String, Object>> wrongAnswers = new ArrayList<>();

    private int currentIndex = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_answer);

        // Bind UI
        questionText = findViewById(R.id.questionText);
        questionImage = findViewById(R.id.questionImage);
        optionsGroup = findViewById(R.id.optionsGroup);
        optA = findViewById(R.id.optionA);
        optB = findViewById(R.id.optionB);
        optC = findViewById(R.id.optionC);
        nextBtn = findViewById(R.id.nextBtn);
        progressText = findViewById(R.id.progressText);

        String lessonId = getIntent().getStringExtra("LESSON_ID");
        lessonRef = FirebaseDatabase.getInstance()
                .getReference("LessonsData")
                .child(lessonId)
                .child("questions");

        loadQuestions();

        nextBtn.setOnClickListener(v -> checkAnswer());
    }

    private void loadQuestions() {
        lessonRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    questions.add((Map<String, Object>) ds.getValue());
                }
                showQuestion();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showQuestion() {
        if (currentIndex >= questions.size()) {
            finishLesson();
            return;
        }

        optionsGroup.clearCheck();

        Map<String, Object> q = questions.get(currentIndex);

        questionText.setText(q.get("question").toString());
        optA.setText(q.get("optionA").toString());
        optB.setText(q.get("optionB").toString());
        optC.setText(q.get("optionC").toString());

        progressText.setText(
                (currentIndex + 1) + " / " + questions.size()
        );
    }

    private void checkAnswer() {
        int selectedId = optionsGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selected = findViewById(selectedId);
        String selectedAnswer = selected.getText().toString();

        Map<String, Object> q = questions.get(currentIndex);
        String correctAnswer = q.get("correctAnswer").toString();

        if (selectedAnswer.equals(correctAnswer)) {
            score++;
        } else {
            q.put("yourAnswer", selectedAnswer);
            wrongAnswers.add(q);
        }

        currentIndex++;
        showQuestion();
    }

    private void finishLesson() {
        // Later → go to LessonResultActivity
        Toast.makeText(this,
                "Score: " + score + "/" + questions.size(),
                Toast.LENGTH_LONG).show();
        finish();
    }
}
