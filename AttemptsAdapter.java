package com.example.mathsprout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class AttemptsAdapter extends RecyclerView.Adapter<AttemptsAdapter.ViewHolder> {
    private ArrayList<LessonAttempt> list;

    public AttemptsAdapter(ArrayList<LessonAttempt> list) { this.list = list; }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attempt, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LessonAttempt item = list.get(position);
        holder.title.setText(item.getLessonName());
        holder.date.setText(item.getDate());
        holder.score.setText(item.getScore() + "/" + item.getTotal());
    }

    @Override public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, score;
        ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.lessonTitleTv);
            date = v.findViewById(R.id.attemptDate);
            score = v.findViewById(R.id.scoreText);
        }
    }
}