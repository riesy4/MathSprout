package com.example.mathsprout;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue; // Added for timestamp
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerLessonActivity extends AppCompatActivity {

    private TextView questionTv, progressTv;
    private RadioGroup optionsGroup;
    private RadioButton optA, optB, optC;
    private Button nextBtn;

    private List<Map<String, Object>> questions;
    private String lessonId;
    private String teacherId;
    private int currentQuestionIndex = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_lesson);

        // Bind UI
        questionTv = findViewById(R.id.questionTv);
        progressTv = findViewById(R.id.progressTv);
        optionsGroup = findViewById(R.id.optionsGroup);
        optA = findViewById(R.id.optA);
        optB = findViewById(R.id.optB);
        optC = findViewById(R.id.optC);
        nextBtn = findViewById(R.id.nextBtn);


        lessonId = getIntent().getStringExtra("LESSON_ID");
        teacherId = getIntent().getStringExtra("TEACHER_ID");
        String jsonQuestions = getIntent().getStringExtra("LESSON_QUESTIONS_JSON");

        if (jsonQuestions != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Map<String, Object>>>(){}.getType();
            questions = gson.fromJson(jsonQuestions, type);
        }

        if (questions != null && !questions.isEmpty()) {
            displayQuestion();
        } else {
            Toast.makeText(this, "Error loading lesson data", Toast.LENGTH_SHORT).show();
            finish();
        }

        nextBtn.setOnClickListener(v -> checkAnswerAndNext());
    }

    private void displayQuestion() {
        optionsGroup.clearCheck();
        Map<String, Object> q = questions.get(currentQuestionIndex);

        progressTv.setText("Question " + (currentQuestionIndex + 1) + " of " + questions.size());
        questionTv.setText((String) q.get("question"));

        List<String> choices = (List<String>) q.get("choices");

        if (choices != null && choices.size() >= 3) {
            optA.setText(choices.get(0));
            optB.setText(choices.get(1));
            optC.setText(choices.get(2));
        }

        if (currentQuestionIndex == questions.size() - 1) {
            nextBtn.setText("Finish Lesson");
        } else {
            nextBtn.setText("Next Question");
        }
    }

    private void checkAnswerAndNext() {
        int selectedId = optionsGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedLetter = "";
        if (selectedId == R.id.optA) selectedLetter = "A";
        else if (selectedId == R.id.optB) selectedLetter = "B";
        else if (selectedId == R.id.optC) selectedLetter = "C";

        String correctAnswer = (String) questions.get(currentQuestionIndex).get("answer");
        if (selectedLetter.equals(correctAnswer)) {
            score++;
        }

        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {
            displayQuestion();
        } else {
            finishLesson();
        }
    }

    private void finishLesson() {
        String childUid = FirebaseAuth.getInstance().getUid();

        if (childUid != null && lessonId != null && teacherId != null) {
            DatabaseReference performanceRef = FirebaseDatabase.getInstance()
                    .getReference("Lessons")
                    .child(teacherId)
                    .child(childUid)
                    .child("completedLessons")
                    .child(lessonId);

            Map<String, Object> result = new HashMap<>();
            result.put("score", score);
            result.put("total", questions.size());
            result.put("timestamp", ServerValue.TIMESTAMP);
            result.put("lessonTitle", getIntent().getStringExtra("LESSON_TITLE"));

            performanceRef.setValue(result).addOnCompleteListener(task -> {
                FirebaseDatabase.getInstance().getReference("Lessons")
                        .child(teacherId).child(childUid).child("activeLessons")
                        .child(lessonId).removeValue();

                Toast.makeText(this, "Great job! Lesson Completed.", Toast.LENGTH_LONG).show();
                finish();
            });
        }
    }
}