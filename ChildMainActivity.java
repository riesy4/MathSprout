package com.example.mathsprout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class ChildMainActivity extends AppCompatActivity {

    private TextView welcomeText;
    private CardView lessonsBtn, quizzesBtn, profileBtn, whiteboardBtn, viewRewards, logoutBtn;
    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_main);

        welcomeText = findViewById(R.id.welcomeText);
        lessonsBtn = findViewById(R.id.lessonsBtn);
        quizzesBtn = findViewById(R.id.quizzesBtn);
        profileBtn = findViewById(R.id.profileBtn);
        whiteboardBtn = findViewById(R.id.whiteboardBtn);
        viewRewards = findViewById(R.id.viewRewards);
        logoutBtn = findViewById(R.id.logoutBtn);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        SessionManager session = new SessionManager(this);
        String childId = session.getChildId();

        if (childId == null) {
            Toast.makeText(this, "Child ID not found. Please login again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            usersRef.child(childId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User childUser = snapshot.getValue(User.class);
                        if (childUser != null && childUser.getName() != null && !childUser.getName().isEmpty()) {
                            // Show the exact name the child registered with
                            welcomeText.setText("Welcome, " + childUser.getName() + "!");
                        } else {
                            welcomeText.setText("Welcome!");
                        }
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        lessonsBtn.setOnClickListener(v -> startActivity(new Intent(this, LessonsActivity.class)));
        quizzesBtn.setOnClickListener(v -> startActivity(new Intent(this, ChildQuizActivity.class)));
        profileBtn.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        whiteboardBtn.setOnClickListener(v -> startActivity(new Intent(this, JoinWhiteboardActivity.class)));
        viewRewards.setOnClickListener(v -> startActivity(new Intent(this, RewardsActivity.class)));

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            session.clearChildId();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}