package com.example.mathsprout;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.QuestionViewHolder> {

    public interface QuestionActionListener {
        void onEdit(Question question, int position);
        void onDelete(Question question, int position);
    }

    private Context context;
    private List<Question> questionList;
    private QuestionActionListener listener;

    public QuestionsAdapter(Context context, List<Question> questionList, QuestionActionListener listener) {
        this.context = context;
        this.questionList = questionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.question_item, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question q = questionList.get(position);

        holder.questionText.setText(q.getQuestionText());
        holder.categoryText.setText("Category: " + q.getCategory());

        if (q.getImageUrl() != null && !q.getImageUrl().isEmpty()) {
            holder.questionImage.setVisibility(View.VISIBLE);
            holder.questionImage.setImageURI(Uri.parse(q.getImageUrl()));
        } else {
            holder.questionImage.setVisibility(View.GONE);
        }

        holder.editBtn.setOnClickListener(v -> listener.onEdit(q, position));
        holder.deleteBtn.setOnClickListener(v -> listener.onDelete(q, position));
    }


    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView questionText, categoryText;
        ImageView questionImage;
        Button editBtn, deleteBtn;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.questionText);
            categoryText = itemView.findViewById(R.id.categoryText);
            questionImage = itemView.findViewById(R.id.questionImage);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
