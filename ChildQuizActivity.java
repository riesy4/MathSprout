package com.example.mathsprout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChildQuizActivity extends AppCompatActivity {
    private TextView txtQuestion, txtProgress, txtStarsLive;
    private RadioGroup radioGroup;
    private RadioButton rbA, rbB, rbC, rbD;
    private Button btnNext;
    private ProgressBar quizProgressBar;
    private List<Quiz> quizList = new ArrayList<>();
    private List<Quiz> wrongAnswers = new ArrayList<>();
    private int currentIndex = 0;
    private int score = 0;
    private int stars = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_quiz);

        txtQuestion = findViewById(R.id.txtQuestion);
        txtProgress = findViewById(R.id.txtProgress);
        txtStarsLive = findViewById(R.id.txtStarsLive);
        quizProgressBar = findViewById(R.id.quizProgressBar);
        radioGroup = findViewById(R.id.radioGroup);
        rbA = findViewById(R.id.rbA);
        rbB = findViewById(R.id.rbB);
        rbC = findViewById(R.id.rbC);
        rbD = findViewById(R.id.rbD);
        btnNext = findViewById(R.id.btnNext);

        generateQuestions();

        if (quizList.size() < 20) {
            Toast.makeText(this, "Not enough questions in pool", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Collections.shuffle(quizList);
        quizList = quizList.subList(0, 20);

        quizProgressBar.setMax(quizList.size());
        showQuestion();

        btnNext.setOnClickListener(v -> {
            if (radioGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
                return;
            }

            checkAnswer();
            currentIndex++;
            if (currentIndex < quizList.size()) {
                showQuestion();
            } else {
                // Quiz Finished
                QuizWrongHolder.wrongList = wrongAnswers;

                // Save attempt to Firebase
                saveQuizAttempt(score, stars, "Mixed Arithmetic Quiz");

                // Navigate to Results
                Intent intent = new Intent(this, ChildQuizResultActivity.class);
                intent.putExtra("score", score);
                intent.putExtra("stars", stars);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showQuestion() {
        radioGroup.clearCheck();
        Quiz quiz = quizList.get(currentIndex);

        txtQuestion.setText(quiz.getQuestion());
        rbA.setText(quiz.getOptionA());
        rbB.setText(quiz.getOptionB());
        rbC.setText(quiz.getOptionC());
        rbD.setText(quiz.getOptionD());

        txtProgress.setText((currentIndex + 1) + "/" + quizList.size());
        txtStarsLive.setText("Stars: " + stars + " ⭐");
        quizProgressBar.setProgress(currentIndex + 1);
    }

    private void checkAnswer() {
        Quiz quiz = quizList.get(currentIndex);

        RadioButton selected = findViewById(radioGroup.getCheckedRadioButtonId());
        String answer = selected.getText().toString();

        if (answer.equals(quiz.getCorrectAnswer())) {
            score++;
            stars += 1;
        } else {
            wrongAnswers.add(quiz);
        }
    }

    private void generateQuestions() {
        Random random = new Random();

        for (int i = 0; i < 40; i++) {
            int a = random.nextInt(10) + 1;
            int b = random.nextInt(10) + 1;
            int operation = random.nextInt(4);

            String question;
            int correct;

            switch (operation) {
                case 0: // Addition
                    question = a + " + " + b + " = ?";
                    correct = a + b;
                    break;
                case 1: // Subtraction
                    question = a + " - " + b + " = ?";
                    correct = a - b;
                    break;
                default:
                    question = a + " + " + b + " = ?";
                    correct = a + b;
            }

            List<String> options = new ArrayList<>();
            options.add(String.valueOf(correct));
            options.add(String.valueOf(correct + 1));
            options.add(String.valueOf(correct - 1));
            options.add(String.valueOf(correct + 2));

            Collections.shuffle(options);

            Quiz quiz = new Quiz(
                    question,
                    options.get(0),
                    options.get(1),
                    options.get(2),
                    options.get(3),
                    String.valueOf(correct)
            );

            quizList.add(quiz);
        }
    }

    private void saveQuizAttempt(int score, int stars, String quizTitle) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String childId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("children");

            String attemptId = String.valueOf(System.currentTimeMillis());

            QuizRecord record = new QuizRecord(score, stars, System.currentTimeMillis(), quizTitle);
            dbRef.child(childId).child("quizzes").child(attemptId).setValue(record);
        }
    }
}