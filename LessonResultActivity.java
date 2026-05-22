package com.example.mathsprout;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class LessonResultActivity extends AppCompatActivity {

    private TextView scoreText, rewardText;
    private ImageView star1, star2, star3;
    private Button finishBtn;
    private RecyclerView wrongAnswersRecycler;

    private int score, total;
    private ArrayList<Map<String, Object>> wrongAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_result);

        // Bind views
        scoreText = findViewById(R.id.scoreText);
        rewardText = findViewById(R.id.rewardText);
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        finishBtn = findViewById(R.id.finishBtn);
        wrongAnswersRecycler = findViewById(R.id.wrongAnswersRecycler);

        // Get score and total
        score = getIntent().getIntExtra("score", 0);
        total = getIntent().getIntExtra("totalQuestions", 0);

        // Deserialize wrong answers JSON safely
        String wrongJson = getIntent().getStringExtra("wrongAnswers");
        if (wrongJson != null) {
            Type type = new TypeToken<ArrayList<Map<String, Object>>>(){}.getType();
            wrongAnswers = new Gson().fromJson(wrongJson, type);
        } else {
            wrongAnswers = new ArrayList<>();
        }

        // Display score and reward
        scoreText.setText("Score: " + score + " / " + total);
        showReward();

        // Setup RecyclerView
        wrongAnswersRecycler.setLayoutManager(new LinearLayoutManager(this));
        WrongAnswerAdapter adapter = new WrongAnswerAdapter(wrongAnswers);
        wrongAnswersRecycler.setAdapter(adapter);

        // Finish button
        finishBtn.setOnClickListener(v -> finish());
    }

    private void showReward() {
        double percentage = (double) score / total * 100;

        if (percentage >= 80) {
            rewardText.setText("Excellent! 🌟🌟🌟");
            star1.setVisibility(ImageView.VISIBLE);
            star2.setVisibility(ImageView.VISIBLE);
            star3.setVisibility(ImageView.VISIBLE);
        } else if (percentage >= 50) {
            rewardText.setText("Good Job! 🌟🌟");
            star1.setVisibility(ImageView.VISIBLE);
            star2.setVisibility(ImageView.VISIBLE);
            star3.setVisibility(ImageView.GONE);
        } else {
            rewardText.setText("Keep Practicing! 🌟");
            star1.setVisibility(ImageView.VISIBLE);
            star2.setVisibility(ImageView.GONE);
            star3.setVisibility(ImageView.GONE);
        }
    }
}
