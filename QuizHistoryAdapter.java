package com.example.mathsprout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class QuizHistoryAdapter extends RecyclerView.Adapter<QuizHistoryAdapter.ViewHolder> {

    private ArrayList<ChildProfileActivity.QuizRecord> quizList;
    private Context context;
    private OnReviewClickListener listener;

    public interface OnReviewClickListener {
        void onReviewClick(ChildProfileActivity.QuizRecord record);
    }

    public QuizHistoryAdapter(Context context, ArrayList<ChildProfileActivity.QuizRecord> quizList, OnReviewClickListener listener) {
        this.context = context;
        this.quizList = quizList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_quiz_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChildProfileActivity.QuizRecord record = quizList.get(position);
        holder.textView.setText("Score: " + record.score + "/10 | Stars: " + record.stars);
        holder.reviewButton.setOnClickListener(v -> listener.onReviewClick(record));
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        Button reviewButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.quizItemText);
            reviewButton = itemView.findViewById(R.id.reviewButton);
        }
    }
}
