package com.example.mathsprout;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.Collections;

public class LessonAttemptDetailActivity extends AppCompatActivity {

    private TextView childNameText, childEmailText, totalLessonsText;
    private EditText searchBar;
    private ProgressBar progressBar;
    private RecyclerView attemptsRecyclerView;

    private AttemptsAdapter adapter;
    private ArrayList<LessonAttempt> attemptsList = new ArrayList<>();
    private ArrayList<LessonAttempt> filteredList = new ArrayList<>();

    private DatabaseReference usersRef, lessonsRef;
    private String myUid, childUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_attempt_detail);

        childNameText = findViewById(R.id.childNameText);
        childEmailText = findViewById(R.id.childEmailText);
        totalLessonsText = findViewById(R.id.totalLessonsText);
        searchBar = findViewById(R.id.searchBar);
        progressBar = findViewById(R.id.progressBar);
        attemptsRecyclerView = findViewById(R.id.attemptsRecyclerView);

        myUid = FirebaseAuth.getInstance().getUid();
        childUid = getIntent().getStringExtra("CHILD_UID");

        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        lessonsRef = FirebaseDatabase.getInstance().getReference("Lessons");

        adapter = new AttemptsAdapter(filteredList);
        attemptsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        attemptsRecyclerView.setAdapter(adapter);

        setupSearch();
        loadChildInfo();
        loadAttempts();
    }

    private void setupSearch() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String text) {
        filteredList.clear();
        for (LessonAttempt item : attemptsList) {
            if (item.getLessonName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadChildInfo() {
        usersRef.child(childUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                childNameText.setText(snapshot.child("name").getValue(String.class));
                childEmailText.setText(snapshot.child("email").getValue(String.class));
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadAttempts() {
        progressBar.setVisibility(View.VISIBLE);
        lessonsRef.child(myUid).child(childUid).child("completedLessons")
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        attemptsList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            // SYNCED KEY: Using "lessonTitle" to match AnswerLessonActivity
                            String title = ds.child("lessonTitle").getValue(String.class);
                            if (title == null) title = ds.child("title").getValue(String.class);

                            int score = 0, total = 0;
                            Object s = ds.child("score").getValue();
                            Object t = ds.child("total").getValue();
                            if (s instanceof Number) score = ((Number) s).intValue();
                            if (t instanceof Number) total = ((Number) t).intValue();

                            attemptsList.add(new LessonAttempt(title, score, total, "Completed"));
                        }
                        Collections.reverse(attemptsList);
                        filteredList.clear();
                        filteredList.addAll(attemptsList);
                        totalLessonsText.setText("Lessons Completed: " + attemptsList.size());
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}