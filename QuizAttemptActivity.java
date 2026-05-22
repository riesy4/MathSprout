package com.example.mathsprout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class QuizAttemptActivity extends AppCompatActivity {

    TextView questionText, scoreText;
    RadioGroup optionsGroup;
    RadioButton option1, option2, option3;
    Button submitBtn;

    Random random = new Random();
    int correctAnswer;
    int score = 0;
    int questionCount = 0;
    int totalQuestions = 10;

    String difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_attempt);

        // Initialize views
        questionText = findViewById(R.id.questionText);
        scoreText = findViewById(R.id.scoreText);
        optionsGroup = findViewById(R.id.optionsGroup);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        submitBtn = findViewById(R.id.submitBtn);

        // Get difficulty from intent
        difficulty = getIntent().getStringExtra("difficulty");

        generateQuestion();
        setOptions();
        updateScoreText();

        submitBtn.setOnClickListener(v -> {
            int selectedId = optionsGroup.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(this, "Please select an answer!", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selected = findViewById(selectedId);
            int answer = Integer.parseInt(selected.getText().toString());

            if (answer == correctAnswer) {
                score++;
            }

            questionCount++;

            if (questionCount < totalQuestions) {
                generateQuestion();
                setOptions();
                optionsGroup.clearCheck();
                updateScoreText();
            } else {
                finishQuiz();
            }
        });
    }

    private void generateQuestion() {
        int maxNumber = 10;

        // Difficulty only affects number range
        switch (difficulty) {
            case "Medium":
                maxNumber = 20;
                break;
            case "Hard":
                maxNumber = 50;
                break;
        }

        // Always include all four operations
        String[] operations = {"+", "-", "×", "÷"};

        int num1 = random.nextInt(maxNumber) + 1;
        int num2 = random.nextInt(maxNumber) + 1;
        String operation = operations[random.nextInt(operations.length)];

        switch (operation) {
            case "+":
                correctAnswer = num1 + num2;
                break;
            case "-":
                correctAnswer = num1 - num2;
                break;
            case "×":
                correctAnswer = num1 * num2;
                break;
            case "÷":
                // Ensure division is clean (no remainder)
                num1 = num1 * num2;
                correctAnswer = num1 / num2;
                break;
        }

        questionText.setText("What is " + num1 + " " + operation + " " + num2 + "?");
    }

    private void setOptions() {
        int wrong1 = correctAnswer + random.nextInt(5) + 1;
        int wrong2 = correctAnswer - (random.nextInt(5) + 1);

        int position = random.nextInt(3);

        RadioButton[] buttons = {option1, option2, option3};
        buttons[position].setText(String.valueOf(correctAnswer));
        buttons[(position + 1) % 3].setText(String.valueOf(wrong1));
        buttons[(position + 2) % 3].setText(String.valueOf(wrong2));
    }

    private void updateScoreText() {
        scoreText.setText("Score: " + score + "/" + totalQuestions);
    }

    private void finishQuiz() {
        Toast.makeText(this, "Quiz Finished! You scored " + score + "/" + totalQuestions, Toast.LENGTH_LONG).show();

        // TODO: Save score to Firebase here

        // After calculating score
        Intent intent = new Intent(QuizAttemptActivity.this, RewardsActivity.class);
        intent.putExtra("score", score); // pass score to RewardsActivity
        startActivity(intent);
        finish(); // close quiz activity
    }
}