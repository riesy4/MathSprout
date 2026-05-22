package com.example.mathsprout;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class ReviewWrongAnswersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WrongAnswerAdapter adapter;
    private ArrayList<Map<String, Object>> wrongAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_wrong_answers);

        recyclerView = findViewById(R.id.wrongAnswersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        wrongAnswers = (ArrayList<Map<String, Object>>) getIntent().getSerializableExtra("WRONG_ANSWERS");
        if (wrongAnswers == null) wrongAnswers = new ArrayList<>();

        adapter = new WrongAnswerAdapter(wrongAnswers);
        recyclerView.setAdapter(adapter);
    }

    private static class WrongAnswerAdapter extends RecyclerView.Adapter<WrongAnswerAdapter.ViewHolder> {

        private final ArrayList<Map<String, Object>> wrongList;

        public WrongAnswerAdapter(ArrayList<Map<String, Object>> wrongList) {
            this.wrongList = wrongList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView tv = new TextView(parent.getContext());
            tv.setPadding(16, 16, 16, 16);
            tv.setTextSize(16f);
            return new ViewHolder(tv);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Map<String, Object> item = wrongList.get(position);
            String question = (String) item.get("question");
            String correctAnswer = (String) item.get("correctAnswer");
            String childAnswer = (String) item.get("childAnswer");

            holder.textView.setText("Q: " + question + "\nYour Answer: " + childAnswer + "\nCorrect Answer: " + correctAnswer);
        }

        @Override
        public int getItemCount() {
            return wrongList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            public ViewHolder(@NonNull TextView itemView) {
                super(itemView);
                textView = itemView;
            }
        }
    }
}
