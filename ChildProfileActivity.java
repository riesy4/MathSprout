package com.example.mathsprout;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChildProfileActivity extends AppCompatActivity {

    TextView totalStarsText, totalPointsText, levelText, badgesText;
    RecyclerView quizRecyclerView;
    ArrayList<QuizRecord> quizHistoryList;
    QuizHistoryAdapter adapter;

    FirebaseAuth mAuth;
    DatabaseReference dbRef, progressRef, achievementRef, quizHistoryRef;
    String childId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_profile);

        totalStarsText = findViewById(R.id.totalStarsText);
        totalPointsText = findViewById(R.id.totalPointsText);
        levelText = findViewById(R.id.levelText);
        badgesText = findViewById(R.id.badgesText);
        quizRecyclerView = findViewById(R.id.quizRecyclerView);

        quizHistoryList = new ArrayList<>();
        adapter = new QuizHistoryAdapter(this, quizHistoryList, record -> {
            // Handle review button click
            Toast.makeText(ChildProfileActivity.this,
                    "Reviewing quiz: Score " + record.score + " Stars " + record.stars,
                    Toast.LENGTH_SHORT).show();
            // Here you can open a new Activity to show wrong answers
        });

        quizRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizRecyclerView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        childId = mAuth.getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference("children");
        progressRef = FirebaseDatabase.getInstance().getReference("child_progress").child(childId);
        achievementRef = FirebaseDatabase.getInstance().getReference("achievements").child(childId);
        quizHistoryRef = dbRef.child(childId).child("quizzes");

        loadProgress();
        loadAchievements();
        fetchQuizHistory();
    }

    private void loadProgress() {
        progressRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalStars = snapshot.child("totalStars").exists() ? snapshot.child("totalStars").getValue(Integer.class) : 0;
                int totalPoints = snapshot.child("totalPoints").exists() ? snapshot.child("totalPoints").getValue(Integer.class) : 0;
                int level = snapshot.child("level").exists() ? snapshot.child("level").getValue(Integer.class) : 1;

                StringBuilder starEmoji = new StringBuilder();
                for (int i = 0; i < totalStars; i++) starEmoji.append("⭐");

                totalStarsText.setText("Stars: " + starEmoji.toString());
                totalPointsText.setText("Total Points: " + totalPoints);
                levelText.setText("Level: " + level);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadAchievements() {
        achievementRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder badges = new StringBuilder();

                if (snapshot.child("firstQuiz").exists()) badges.append("🥇 First Quiz Completed\n");
                if (snapshot.child("score80").exists()) badges.append("🌟 Score 80+ Badge\n");
                if (snapshot.child("mathStar").exists()) badges.append("🚀 Math Star Badge\n");

                if (badges.length() == 0) badges.append("No badges yet");
                badgesText.setText("Badges:\n" + badges.toString());
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void fetchQuizHistory() {
        quizHistoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quizHistoryList.clear();

                for (DataSnapshot quizSnapshot : snapshot.getChildren()) {
                    QuizRecord record = quizSnapshot.getValue(QuizRecord.class);
                    if (record != null) {
                        quizHistoryList.add(record);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public static class QuizRecord {
        public int score;
        public int stars;

        public QuizRecord() {}
        public QuizRecord(int score, int stars) {
            this.score = score;
            this.stars = stars;
        }
    }
}
