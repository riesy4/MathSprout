package com.example.mathsprout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LessonsActivity extends AppCompatActivity {

    private RecyclerView lessonsRv;
    private LinearLayout emptyState;
    private List<Map<String, Object>> lessonsList = new ArrayList<>();
    private LessonsAdapter adapter;
    private DatabaseReference usersRef, lessonsRef;
    private String myUid, teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);
        lessonsRv = findViewById(R.id.lessonsRv);
        emptyState = findViewById(R.id.emptyState);
        myUid = FirebaseAuth.getInstance().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        lessonsRef = FirebaseDatabase.getInstance().getReference("Lessons");

        setupRecyclerView();
        findTeacherAndLoadLessons();
    }

    private void setupRecyclerView() {
        lessonsRv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new LessonsAdapter(lessonsList, (Map<String, Object> lesson) -> {
            Intent intent = new Intent(this, AnswerLessonActivity.class);

            // Using String.valueOf() prevents the 'Cannot resolve' error
            intent.putExtra("LESSON_ID", String.valueOf(lesson.get("lessonId")));
            intent.putExtra("LESSON_TITLE", String.valueOf(lesson.get("title")));
            intent.putExtra("TEACHER_ID", teacherId);

            Gson gson = new Gson();
            String json = gson.toJson(lesson.get("questions"));
            intent.putExtra("LESSON_QUESTIONS_JSON", json);

            startActivity(intent);
        });
        lessonsRv.setAdapter(adapter);
    }

    private void findTeacherAndLoadLessons() {
        usersRef.child(myUid).child("linkedTeacher").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    teacherId = snapshot.getValue(String.class);
                    loadLessonsFromTeacher(teacherId);
                } else {
                    showEmptyState(true);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadLessonsFromTeacher(String tId) {
        lessonsRef.child(tId).child(myUid).child("activeLessons")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        lessonsList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            // FIX: Proper casting when pulling from Firebase
                            Map<String, Object> lesson = (Map<String, Object>) ds.getValue();
                            if (lesson != null) {
                                lessonsList.add(lesson);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        showEmptyState(lessonsList.isEmpty());
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void showEmptyState(boolean isEmpty) {
        if (isEmpty) {
            emptyState.setVisibility(View.VISIBLE);
            lessonsRv.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            lessonsRv.setVisibility(View.VISIBLE);
        }
    }
}