package com.example.mathsprout;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

public class ChildAchievementsActivity extends AppCompatActivity {

    TextView txtStars, txtLevel, txtPoints, txtQuizzesCompleted, txtBadges;

    boolean firstQuizUnlocked = false;
    boolean score80Unlocked = false;
    boolean mathStarUnlocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_achievements);

        txtStars = findViewById(R.id.txtStars);
        txtLevel = findViewById(R.id.txtLevel);
        txtPoints = findViewById(R.id.txtPoints);
        txtQuizzesCompleted = findViewById(R.id.txtQuizzesCompleted);
        txtBadges = findViewById(R.id.txtBadges);

        String childId = "child1"; // Replace with actual childId

        DatabaseReference progressRef = FirebaseDatabase.getInstance()
                .getReference("child_progress").child(childId);

        DatabaseReference achievementRef = FirebaseDatabase.getInstance()
                .getReference("achievements").child(childId);

        DatabaseReference attemptsRef = FirebaseDatabase.getInstance()
                .getReference("quiz_attempts").child(childId).child("usedQuestionIds");

        // Load progress (stars, points, level)
        progressRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalStars = snapshot.child("totalStars").exists() ? snapshot.child("totalStars").getValue(Integer.class) : 0;
                int level = snapshot.child("level").exists() ? snapshot.child("level").getValue(Integer.class) : 1;
                int totalPoints = snapshot.child("totalPoints").exists() ? snapshot.child("totalPoints").getValue(Integer.class) : 0;

                StringBuilder starEmoji = new StringBuilder();
                for (int i = 0; i < totalStars; i++) starEmoji.append("⭐");

                txtStars.setText("Stars: " + starEmoji.toString());
                txtLevel.setText("Level: " + level);
                txtPoints.setText("Total Points: " + totalPoints);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Count quizzes completed
        attemptsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int quizzesCompleted = (int) snapshot.getChildrenCount();
                txtQuizzesCompleted.setText("Quizzes Completed: " + quizzesCompleted);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Load badges
        achievementRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder badges = new StringBuilder();

                if(snapshot.child("firstQuiz").exists() && !firstQuizUnlocked) {
                    badges.append("🥇 First Quiz Completed\n");
                    firstQuizUnlocked = true;
                    Toast.makeText(ChildAchievementsActivity.this, "New Badge Unlocked! 🥇", Toast.LENGTH_SHORT).show();
                }
                if(snapshot.child("score80").exists() && !score80Unlocked) {
                    badges.append("🌟 Score 80+ Badge\n");
                    score80Unlocked = true;
                    Toast.makeText(ChildAchievementsActivity.this, "New Badge Unlocked! 🌟", Toast.LENGTH_SHORT).show();
                }
                if(snapshot.child("mathStar").exists() && !mathStarUnlocked) {
                    badges.append("🚀 Math Star Badge\n");
                    mathStarUnlocked = true;
                    Toast.makeText(ChildAchievementsActivity.this, "New Badge Unlocked! 🚀", Toast.LENGTH_SHORT).show();
                }

                if(badges.length() == 0) badges.append("None Yet");
                txtBadges.setText("Badges:\n" + badges.toString());
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
