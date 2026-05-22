package com.example.mathsprout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class EducatorPerformanceActivity extends AppCompatActivity {

    private RecyclerView performanceRecyclerView;
    private ProgressBar progressBar;
    private PerformanceAdapter adapter;
    private ArrayList<ChildPerformance> performanceList = new ArrayList<>();
    private DatabaseReference usersRef, lessonsRef;
    private String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_educator_performance);

        performanceRecyclerView = findViewById(R.id.performanceRecyclerView);
        progressBar = findViewById(R.id.progressBar);

        myUid = FirebaseAuth.getInstance().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        lessonsRef = FirebaseDatabase.getInstance().getReference("Lessons");

        adapter = new PerformanceAdapter(performanceList);
        performanceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        performanceRecyclerView.setAdapter(adapter);
        loadPerformance();
    }

    private void loadPerformance() {
        progressBar.setVisibility(View.VISIBLE);

        Query linkedStudentsQuery = usersRef.orderByChild("linkedTeacher").equalTo(myUid);

        linkedStudentsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                performanceList.clear();

                if (!snapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    return;
                }

                final long totalStudents = snapshot.getChildrenCount();
                final int[] loadedCount = {0};

                for (DataSnapshot studentSnap : snapshot.getChildren()) {
                    String childUid = studentSnap.getKey();
                    String childName = studentSnap.child("name").getValue(String.class);
                    String childEmail = studentSnap.child("email").getValue(String.class);
                    String displayName = (childName != null && !childName.isEmpty()) ? childName : childEmail;

                    lessonsRef.child(myUid).child(childUid).child("completedLessons")
                            .limitToLast(1)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot resultSnapshot) {
                                    int score = 0;
                                    int total = 0;
                                    String reward = "No attempts";

                                    for (DataSnapshot lastResult : resultSnapshot.getChildren()) {
                                        Integer s = lastResult.child("score").getValue(Integer.class);
                                        Integer t = lastResult.child("total").getValue(Integer.class);
                                        score = (s != null) ? s : 0;
                                        total = (t != null) ? t : 0;
                                        reward = "Latest Score";
                                    }

                                    performanceList.add(new ChildPerformance(childUid, displayName, score, total, reward));

                                    loadedCount[0]++;
                                    if (loadedCount[0] >= totalStudents) {
                                        adapter.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }

                                @Override public void onCancelled(@NonNull DatabaseError error) {}
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private class PerformanceAdapter extends RecyclerView.Adapter<PerformanceAdapter.PerformanceViewHolder> {
        private ArrayList<ChildPerformance> list;
        public PerformanceAdapter(ArrayList<ChildPerformance> list) { this.list = list; }

        @NonNull @Override
        public PerformanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_child_performance, parent, false);
            return new PerformanceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PerformanceViewHolder holder, int position) {
            ChildPerformance cp = list.get(position);
            holder.childName.setText(cp.getName());
            holder.scoreText.setText(cp.getScore() + " / " + cp.getTotal());
            holder.rewardText.setText(cp.getReward());

            holder.itemView.setOnClickListener(v -> {
                Intent i = new Intent(EducatorPerformanceActivity.this, LessonAttemptDetailActivity.class);
                i.putExtra("CHILD_UID", cp.getUid());
                startActivity(i);
            });
        }

        @Override public int getItemCount() { return list.size(); }

        class PerformanceViewHolder extends RecyclerView.ViewHolder {
            TextView childName, scoreText, rewardText;
            public PerformanceViewHolder(@NonNull View itemView) {
                super(itemView);
                childName = itemView.findViewById(R.id.childName);
                scoreText = itemView.findViewById(R.id.scoreText);
                rewardText = itemView.findViewById(R.id.rewardText);
            }
        }
    }
}