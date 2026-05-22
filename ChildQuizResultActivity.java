package com.example.mathsprout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class ChildQuizResultActivity extends AppCompatActivity {
    TextView txtScore, txtStars, txtMessage;
    ListView listWrong;
    Button btnViewRewards;
    List<Quiz> wrongAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_quiz_result);

        txtScore = findViewById(R.id.txtScore);
        txtStars = findViewById(R.id.txtStars);
        txtMessage = findViewById(R.id.txtMessage);
        listWrong = findViewById(R.id.listWrong);
        btnViewRewards = findViewById(R.id.btnViewRewards);

        int score = getIntent().getIntExtra("score", 0);
        int stars = getIntent().getIntExtra("stars", 0);

        txtScore.setText("Score: " + score + "/20");
        txtStars.setText("You earned " + stars + " ⭐");

        if (stars == 20) {
            txtMessage.setText("Perfect Score! 🎉");
        } else if (stars >= 15) {
            txtMessage.setText("Excellent work! 👍");
        } else if (stars >= 10) {
            txtMessage.setText("Good job! Keep practicing!");
        } else {
            txtMessage.setText("Keep trying, you’ll improve! 🌱");
        }

        wrongAnswers = QuizWrongHolder.wrongList;
        if (wrongAnswers != null && !wrongAnswers.isEmpty()) {
            String[] wrongTexts = new String[wrongAnswers.size()];
            for (int i = 0; i < wrongAnswers.size(); i++) {
                Quiz q = wrongAnswers.get(i);

                wrongTexts[i] = "Q: " + q.getQuestion() + "\nAns: " + q.getCorrectAnswer();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, wrongTexts);
            listWrong.setAdapter(adapter);
        } else {
            listWrong.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, new String[]{"Perfect! No mistakes found."}));
        }
        QuizWrongHolder.wrongList = null;

        btnViewRewards.setOnClickListener(v -> {
            Intent intent = new Intent(ChildQuizResultActivity.this, RewardsActivity.class);
            intent.putExtra("score", score);
            intent.putExtra("stars", stars);
            startActivity(intent);
            finish();
        });
    }
}