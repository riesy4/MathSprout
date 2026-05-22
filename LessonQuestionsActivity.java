package com.example.mathsprout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.gson.Gson;

import java.util.*;

public class LessonQuestionsActivity extends AppCompatActivity {

    private TextView questionTv;
    private ImageView questionImage;
    private RadioGroup optionsGroup;
    private RadioButton optionA, optionB, optionC;
    private Button nextBtn;

    private List<Map<String, Object>> questions = new ArrayList<>();
    private int index = 0;
    private int score = 0;

    private List<Map<String, Object>> wrongAnswers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_questions);

        questionTv = findViewById(R.id.questionTv);
        questionImage = findViewById(R.id.questionImage);
        optionsGroup = findViewById(R.id.optionsGroup);
        optionA = findViewById(R.id.optionA);
        optionB = findViewById(R.id.optionB);
        optionC = findViewById(R.id.optionC);
        nextBtn = findViewById(R.id.nextBtn);

        String category = getIntent().getStringExtra("category");

        loadQuestions(category);

        nextBtn.setOnClickListener(v -> checkAnswer());
    }

    private void loadQuestions(String category) {
        String educatorUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Lessons")
                .child(educatorUid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questions.clear();
                for (DataSnapshot lessonSnap : snapshot.getChildren()) {
                    for (DataSnapshot qSnap : lessonSnap.getChildren()) {
                        Map<String, Object> q = (Map<String, Object>) qSnap.getValue();
                        if (q != null && category.equals(q.get("category"))) {
                            questions.add(q);
                        }
                    }
                }
                showQuestion();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LessonQuestionsActivity.this, "Failed to load questions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showQuestion() {
        if (index >= questions.size()) {
            // Navigate to LessonResultActivity
            Intent intent = new Intent(LessonQuestionsActivity.this, LessonResultActivity.class);
            intent.putExtra("score", score);
            intent.putExtra("totalQuestions", questions.size());

            // Convert wrongAnswers to JSON
            String wrongJson = new Gson().toJson(wrongAnswers);
            intent.putExtra("wrongAnswers", wrongJson);

            startActivity(intent);
            finish();
            return;
        }

        Map<String, Object> q = questions.get(index);

        questionTv.setText(q.get("question").toString());
        optionA.setText(q.get("optionA").toString());
        optionB.setText(q.get("optionB").toString());
        optionC.setText(q.get("optionC").toString());

        optionsGroup.clearCheck();

        if (q.containsKey("image") && q.get("image") != null) {
            questionImage.setVisibility(View.VISIBLE);
            String imageUrl = q.get("image").toString();
            Glide.with(this)
                    .load(imageUrl)
                    .error(android.R.drawable.ic_dialog_alert)
                    .into(questionImage);
        } else {
            questionImage.setVisibility(View.GONE);
        }
    }

    private void checkAnswer() {
        int checkedId = optionsGroup.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, "Select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        String selected = checkedId == R.id.optionA ? "A"
                : checkedId == R.id.optionB ? "B" : "C";

        String correct = questions.get(index).get("correct").toString();

        if (selected.equals(correct)) {
            score++;
        } else {
            // Save wrong answer with extra info
            Map<String, Object> wrong = new HashMap<>(questions.get(index));
            wrong.put("childAnswer", selected);
            wrong.put("correctAnswer", correct);
            wrongAnswers.add(wrong);
        }

        index++;
        showQuestion();
    }
}
