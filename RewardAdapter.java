package com.example.mathsprout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.RewardViewHolder> {

    private List<QuizRecord> rewardList;

    public RewardAdapter(List<QuizRecord> rewardList) {
        this.rewardList = rewardList;
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reward, parent, false);
        return new RewardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        QuizRecord record = rewardList.get(position);

        // Quiz title
        holder.quizTitleTv.setText("Quiz: " + record.quizTitle);

        // Stars earned (convert to emoji string)
        holder.starsTv.setText("Stars: " + getStarsString(record.stars));

        // Format timestamp to readable date
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        String dateStr = sdf.format(new Date(record.answeredAt));
        holder.answeredAtTv.setText("Answered: " + dateStr);
    }

    @Override
    public int getItemCount() {
        return rewardList.size();
    }

    private String getStarsString(int stars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stars; i++) {
            sb.append("⭐");
        }
        return sb.toString();
    }

    static class RewardViewHolder extends RecyclerView.ViewHolder {
        TextView quizTitleTv, starsTv, answeredAtTv;

        RewardViewHolder(View itemView) {
            super(itemView);
            quizTitleTv = itemView.findViewById(R.id.quizTitleTv);
            starsTv = itemView.findViewById(R.id.starsTv);
            answeredAtTv = itemView.findViewById(R.id.answeredAtTv);
        }
    }
}