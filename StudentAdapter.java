package com.example.mathsprout;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Child> studentList;

    public StudentAdapter(List<Child> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Child student = studentList.get(position);
        holder.studentEmail.setText(student.getEmail());

        // Fitur hapus murid saat ditekan lama (Long Click)
        holder.itemView.setOnLongClickListener(v -> {
            showRemoveDialog(v.getContext(), student);
            return true;
        });
    }

    private void showRemoveDialog(Context context, Child student) {
        new AlertDialog.Builder(context)
                .setTitle("Remove Student")
                .setMessage("Are you sure you want to remove " + student.getEmail() + " from your class?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    // Menghapus field linkedTeacher di node murid tersebut
                    DatabaseReference linkedTeacherRef = FirebaseDatabase.getInstance()
                            .getReference("Users")
                            .child(student.getUid())
                            .child("linkedTeacher");

                    linkedTeacherRef.removeValue().addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Student removed successfully", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView studentEmail;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentEmail = itemView.findViewById(R.id.studentEmail);
        }
    }
}