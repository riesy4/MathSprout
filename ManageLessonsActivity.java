package com.example.mathsprout;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.*;

public class ManageLessonsActivity extends AppCompatActivity {

    private EditText titleEt, questionEt, optionAEt, optionBEt, optionCEt;
    private Spinner correctSpinner, categorySpinner;
    private Button addQuestionBtn, assignLessonBtn;
    private CheckBox selectAllCb;
    private RecyclerView childRecyclerView, questionsRecyclerView;
    private TextView noStudentsTv;

    private QuestionsAdapter questionsAdapter;
    private final List<Map<String, Object>> questionList = new ArrayList<>();
    private final List<Child> childList = new ArrayList<>();
    private ChildAdapter childAdapter;

    private DatabaseReference usersRef, lessonsRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_lessons);

        initViews();
        setupFirebase();
        setupSpinners();
        setupChildrenList();
        setupQuestionsList();

        addQuestionBtn.setOnClickListener(v -> addQuestion());
        assignLessonBtn.setOnClickListener(v -> assignLesson());

        selectAllCb.setOnCheckedChangeListener((b, checked) -> {
            for (Child c : childList) c.setSelected(checked);
            childAdapter.notifyDataSetChanged();
        });
    }

    private void initViews() {
        titleEt = findViewById(R.id.lessonTitleEt);
        questionEt = findViewById(R.id.questionEt);
        optionAEt = findViewById(R.id.optionAEt);
        optionBEt = findViewById(R.id.optionBEt);
        optionCEt = findViewById(R.id.optionCEt);
        correctSpinner = findViewById(R.id.correctSpinner);
        categorySpinner = findViewById(R.id.categorySpinner);
        addQuestionBtn = findViewById(R.id.addQuestionBtn);
        assignLessonBtn = findViewById(R.id.assignLessonBtn);
        selectAllCb = findViewById(R.id.selectAllCb);
        childRecyclerView = findViewById(R.id.childrenRecyclerView);
        questionsRecyclerView = findViewById(R.id.questionsRecyclerView);
        noStudentsTv = findViewById(R.id.noStudentsTv);
    }

    private void setupFirebase() {
        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        lessonsRef = FirebaseDatabase.getInstance().getReference("Lessons");
    }

    private void setupSpinners() {
        ArrayAdapter<String> correctAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, Arrays.asList("A", "B", "C"));
        correctAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        correctSpinner.setAdapter(correctAdapter);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                Arrays.asList("Addition", "Subtraction", "Mixed"));
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void setupChildrenList() {
        childAdapter = new ChildAdapter(childList);
        childRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        childRecyclerView.setAdapter(childAdapter);

        String myUid = auth.getUid();
        usersRef.orderByChild("linkedTeacher").equalTo(myUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        childList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String email = ds.child("email").getValue(String.class);
                            String name = ds.child("name").getValue(String.class);
                            String displayName = (name != null && !name.isEmpty()) ? name : email;
                            childList.add(new Child(ds.getKey(), displayName, email));
                        }
                        childAdapter.notifyDataSetChanged();
                        if (noStudentsTv != null) {
                            noStudentsTv.setVisibility(childList.isEmpty() ? View.VISIBLE : View.GONE);
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void setupQuestionsList() {
        questionsAdapter = new QuestionsAdapter(questionList, new QuestionsAdapter.Callback() {
            @Override public void onEdit(int position) { showEditDialog(position); }
            @Override public void onDelete(int position) {
                questionList.remove(position);
                questionsAdapter.notifyItemRemoved(position);
            }
        });
        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        questionsRecyclerView.setAdapter(questionsAdapter);
    }

    private void addQuestion() {
        String q = questionEt.getText().toString().trim();
        String a = optionAEt.getText().toString().trim();
        String b = optionBEt.getText().toString().trim();
        String c = optionCEt.getText().toString().trim();

        if (TextUtils.isEmpty(q) || TextUtils.isEmpty(a) || TextUtils.isEmpty(b) || TextUtils.isEmpty(c)) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> question = new HashMap<>();
        question.put("id", UUID.randomUUID().toString());
        question.put("question", q);
        question.put("choices", Arrays.asList(a, b, c));
        question.put("answer", correctSpinner.getSelectedItem().toString());
        question.put("category", categorySpinner.getSelectedItem().toString());

        questionList.add(question);
        questionsAdapter.notifyItemInserted(questionList.size() - 1);
        clearInputs();
    }

    private void clearInputs() {
        questionEt.setText("");
        optionAEt.setText("");
        optionBEt.setText("");
        optionCEt.setText("");
    }

    private void assignLesson() {
        String title = titleEt.getText().toString().trim();
        if (TextUtils.isEmpty(title) || questionList.isEmpty()) {
            Toast.makeText(this, "Check title and questions", Toast.LENGTH_SHORT).show();
            return;
        }

        String myUid = auth.getUid();
        int count = 0;
        for (Child c : childList) {
            if (c.isSelected()) {
                DatabaseReference ref = lessonsRef.child(myUid).child(c.getUid()).child("activeLessons").push();
                Map<String, Object> lesson = new HashMap<>();
                lesson.put("lessonId", ref.getKey());
                lesson.put("title", title);
                lesson.put("category", categorySpinner.getSelectedItem().toString());
                lesson.put("questions", questionList);
                lesson.put("assignedBy", myUid);
                lesson.put("timestamp", ServerValue.TIMESTAMP);
                ref.setValue(lesson);
                count++;
            }
        }
        Toast.makeText(this, "Assigned to " + count + " children", Toast.LENGTH_SHORT).show();
        questionList.clear();
        questionsAdapter.notifyDataSetChanged();
        titleEt.setText("");
    }

    private void showEditDialog(int position) {
        Map<String, Object> q = questionList.get(position);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_question, null);
        EditText dqEt = dialogView.findViewById(R.id.editQuestionEt);
        EditText daEt = dialogView.findViewById(R.id.editOptionAEt);
        EditText dbEt = dialogView.findViewById(R.id.editOptionBEt);
        EditText dcEt = dialogView.findViewById(R.id.editOptionCEt);
        Spinner dCorrect = dialogView.findViewById(R.id.editCorrectSpinner);

        dqEt.setText((String) q.get("question"));
        List<String> choices = (List<String>) q.get("choices");
        daEt.setText(choices.get(0));
        dbEt.setText(choices.get(1));
        dcEt.setText(choices.get(2));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Arrays.asList("A", "B", "C"));
        dCorrect.setAdapter(adapter);
        dCorrect.setSelection(Arrays.asList("A","B","C").indexOf(q.get("answer")));

        new AlertDialog.Builder(this)
                .setTitle("Edit Question")
                .setView(dialogView)
                .setPositiveButton("Save", (d, w) -> {
                    q.put("question", dqEt.getText().toString());
                    q.put("choices", Arrays.asList(daEt.getText().toString(), dbEt.getText().toString(), dcEt.getText().toString()));
                    q.put("answer", dCorrect.getSelectedItem().toString());
                    questionsAdapter.notifyItemChanged(position);
                }).show();
    }

    static class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.QViewHolder> {
        interface Callback { void onEdit(int pos); void onDelete(int pos); }
        private final List<Map<String, Object>> data;
        private final Callback callback;

        QuestionsAdapter(List<Map<String, Object>> data, Callback cb) { this.data = data; this.callback = cb; }

        @NonNull @Override
        public QViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            return new QViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_question_editor, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull QViewHolder h, int pos) {
            Map<String, Object> q = data.get(pos);
            h.questionTv.setText((String) q.get("question"));
            h.editBtn.setOnClickListener(v -> callback.onEdit(pos));
            h.deleteBtn.setOnClickListener(v -> callback.onDelete(pos));
        }

        @Override public int getItemCount() { return data.size(); }

        static class QViewHolder extends RecyclerView.ViewHolder {
            TextView questionTv; ImageButton editBtn, deleteBtn;
            QViewHolder(View v) {
                super(v);
                questionTv = v.findViewById(R.id.qQuestion);
                editBtn = v.findViewById(R.id.qEditBtn);
                deleteBtn = v.findViewById(R.id.qDeleteBtn);
            }
        }
    }
}