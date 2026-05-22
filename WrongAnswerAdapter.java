package com.example.mathsprout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class WrongAnswerAdapter extends RecyclerView.Adapter<WrongAnswerAdapter.WrongAnswerViewHolder> {

    private List<Map<String, Object>> wrongAnswers;

    public WrongAnswerAdapter(List<Map<String, Object>> wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    @NonNull
    @Override
    public WrongAnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wrong_answer, parent, false);
        return new WrongAnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WrongAnswerViewHolder holder, int position) {
        Map<String, Object> wrong = wrongAnswers.get(position);

        String question = (String) wrong.get("question");
        String correctAnswer = (String) wrong.get("correctAnswer");
        String childAnswer = (String) wrong.get("childAnswer");

        holder.questionText.setText("Q: " + question);
        holder.correctAnswerText.setText("Correct: " + correctAnswer);
        holder.childAnswerText.setText("Your Answer: " + childAnswer);
    }

    @Override
    public int getItemCount() {
        return wrongAnswers.size();
    }

    static class WrongAnswerViewHolder extends RecyclerView.ViewHolder {

        TextView questionText, correctAnswerText, childAnswerText;

        public WrongAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.questionText);
            correctAnswerText = itemView.findViewById(R.id.correctAnswerText);
            childAnswerText = itemView.findViewById(R.id.childAnswerText);
        }
    }
}
