package com.example.mathsprout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class RewardsActivity extends AppCompatActivity {
    private TextView starsText, messageText, totalStarsText;
    private RecyclerView rewardsRecyclerView;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private List<QuizRecord> rewardList;
    private RewardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);
        starsText = findViewById(R.id.starsText);
        messageText = findViewById(R.id.messageText);
        totalStarsText = findViewById(R.id.totalStarsText);
        rewardsRecyclerView = findViewById(R.id.rewardsRecyclerView);
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("children");

        int stars = getIntent().getIntExtra("stars", 0);

        starsText.setText("Stars Earned: " + stars + " " + getStarsEmoji(stars));
        messageText.setText(getMessage(stars));

        rewardList = new ArrayList<>();
        adapter = new RewardAdapter(rewardList);
        rewardsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rewardsRecyclerView.setAdapter(adapter);

        loadRewardsHistory();
    }

    private String getStarsEmoji(int stars) {
        if (stars == 0) return "🌱";
        return "⭐";
    }

    private String getMessage(int stars) {
        if (stars >= 20) return "Perfect Achievement! 🎉";
        if (stars >= 15) return "Excellent work! 👍";
        if (stars >= 10) return "Good job! Keep practicing!";
        return "Keep trying, you’ll improve! 🌱";
    }

    private void loadRewardsHistory() {
        if (mAuth.getCurrentUser() == null) return;
        String childId = mAuth.getCurrentUser().getUid();

        dbRef.child(childId).child("quizzes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rewardList.clear();
                int totalStars = 0;

                for (DataSnapshot snap : snapshot.getChildren()) {
                    QuizRecord record = snap.getValue(QuizRecord.class);
                    if (record != null) {
                        rewardList.add(record);
                        totalStars += record.stars;
                    }
                }

                adapter.notifyDataSetChanged();

                totalStarsText.setText("Total Stars Earned: " + totalStars);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RewardsActivity.this, "Error loading rewards", Toast.LENGTH_SHORT).show();
            }
        });
    }
}