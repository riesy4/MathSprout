package com.example.mathsprout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;

public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.LessonViewHolder> {

    private final List<Map<String, Object>> lessons;
    private final OnLessonClickListener listener;

    // Interface to handle clicks in LessonsActivity
    public interface OnLessonClickListener {
        void onLessonKey(Map<String, Object> lesson);
    }

    public LessonsAdapter(List<Map<String, Object>> lessons, OnLessonClickListener listener) {
        this.lessons = lessons;
        this.listener = listener;
    }

    @NonNull @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use the item_lesson.xml layout we discussed
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Map<String, Object> lesson = lessons.get(position);

        // Map the Title and Category to your UI
        holder.titleTv.setText(String.valueOf(lesson.get("title")));
        holder.categoryTv.setText(String.valueOf(lesson.get("category")));

        // Handle the click to start the lesson
        holder.itemView.setOnClickListener(v -> listener.onLessonKey(lesson));
    }

    @Override public int getItemCount() { return lessons.size(); }

    static class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView titleTv, categoryTv;

        LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ensure these IDs match your item_lesson.xml
            titleTv = itemView.findViewById(R.id.lessonTitleTv);
            categoryTv = itemView.findViewById(R.id.lessonCategoryTv);
        }
    }
}