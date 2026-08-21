package com.example.mathsprout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.*;

public class EducatorViewPerformanceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StudentPerformanceAdapter adapter;
    private List<StudentPerformance> fullList = new ArrayList<>();
    private List<StudentPerformance> filteredList = new ArrayList<>();
    private DatabaseReference usersRef, lessonsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_educator_view_performance);

        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        lessonsRef = FirebaseDatabase.getInstance().getReference("Lessons");

        recyclerView = findViewById(R.id.studentsPerformanceRv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentPerformanceAdapter(filteredList);
        recyclerView.setAdapter(adapter);

        setupSearch();
        loadStudents();
    }

    private void setupSearch() {
        SearchView searchView = findViewById(R.id.studentSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            for (StudentPerformance s : fullList) {
                if (s.name != null && s.name.toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(s);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadStudents() {
        usersRef.orderByChild("role").equalTo("Child").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fullList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String uid = ds.getKey();
                    String name = ds.child("name").getValue(String.class);
                    StudentPerformance student = new StudentPerformance(uid, name);
                    fullList.add(student);
                    fetchStats(student);
                }
                filter(""); 
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    private void fetchStats(StudentPerformance student) {
        lessonsRef.child(student.uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int completed = (int) snapshot.child("completed").getChildrenCount();
                int total = (int) snapshot.child("activeLessons").getChildrenCount();
                student.completedCount = completed;
                student.totalCount = total;
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }

    private void showDetailDialog(StudentPerformance student) {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_student_details, null);
        TextView titleTv = v.findViewById(R.id.detailTitleTv);
        LinearLayout container = v.findViewById(R.id.detailsContainer);
        titleTv.setText(student.name + "'s Progress");

        
        lessonsRef.child(student.uid).child("activeLessons").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot activeSnap) {
                Map<String, String> titles = new HashMap<>();
                for (DataSnapshot ds : activeSnap.getChildren()) {
                    titles.put(ds.child("lessonId").getValue(String.class), ds.child("title").getValue(String.class));
                }

                lessonsRef.child(student.uid).child("completed").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot completeSnap) {
                        container.removeAllViews();
                        for (DataSnapshot ds : completeSnap.getChildren()) {
                            View row = LayoutInflater.from(EducatorViewPerformanceActivity.this).inflate(R.layout.item_lesson_score_row, container, false);
                            String title = titles.getOrDefault(ds.getKey(), "Unknown Lesson");
                            ((TextView)row.findViewById(R.id.rowLessonName)).setText(title);
                            ((TextView)row.findViewById(R.id.rowScore)).setText(ds.child("score").getValue() + " / " + ds.child("total").getValue() + " ⭐");
                            container.addView(row);
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) {}
                });
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });

        new AlertDialog.Builder(this).setView(v).setPositiveButton("Close", null).show();
    }

  

    static class StudentPerformance {
        String uid, name;
        int completedCount, totalCount;
        StudentPerformance(String u, String n) { uid = u; name = n; }
    }

    class StudentPerformanceAdapter extends RecyclerView.Adapter<StudentPerformanceAdapter.ViewHolder> {
        List<StudentPerformance> list;
        StudentPerformanceAdapter(List<StudentPerformance> l) { list = l; }

        @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            return new ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_student_performance, p, false));
        }

        @Override public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            StudentPerformance s = list.get(pos);
            h.name.setText(s.name);
            h.stats.setText("Lessons Finished: " + s.completedCount + " / " + s.totalCount);
            h.itemView.setOnClickListener(v -> showDetailDialog(s));
        }

        @Override public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, stats;
            ViewHolder(View v) { super(v); name = v.findViewById(R.id.studentNameTv); stats = v.findViewById(R.id.studentStatsTv); }
        }
    }
}
