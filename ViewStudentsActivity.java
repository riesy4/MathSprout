package com.example.mathsprout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewStudentsActivity extends AppCompatActivity {

    private RecyclerView studentsRecyclerView;
    private StudentAdapter studentAdapter;
    private ArrayList<Child> studentList;
    private ArrayList<Child> filteredList;
    private EditText searchStudents;
    private LinearLayout emptyStateLayout;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_students);

        studentsRecyclerView = findViewById(R.id.studentsRecyclerView);
        searchStudents = findViewById(R.id.searchStudents);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        studentList = new ArrayList<>();
        filteredList = new ArrayList<>();

        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentAdapter = new StudentAdapter(filteredList);
        studentsRecyclerView.setAdapter(studentAdapter);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            myUid = mAuth.getCurrentUser().getUid();
        }
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        setupSearch();
        loadStudents();
    }

    private void setupSearch() {
        searchStudents.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String text) {
        filteredList.clear();
        for (Child child : studentList) {
            // Search by Name or Email
            if (child.getName().toLowerCase().contains(text.toLowerCase()) ||
                    child.getEmail().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(child);
            }
        }
        studentAdapter.notifyDataSetChanged();
        updateEmptyVisibility();
    }

    private void loadStudents() {
        Query linkedStudentsQuery = usersRef.orderByChild("linkedTeacher").equalTo(myUid);

        linkedStudentsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String uid = ds.getKey();
                    String email = ds.child("email").getValue(String.class);
                    String name = ds.child("name").getValue(String.class);
                    String displayName = (name != null && !name.isEmpty()) ? name : email;

                    studentList.add(new Child(uid, displayName, email));
                }

                filteredList.clear();
                filteredList.addAll(studentList);
                studentAdapter.notifyDataSetChanged();

                updateEmptyVisibility();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewStudentsActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyVisibility() {
        if (filteredList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            studentsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            studentsRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}