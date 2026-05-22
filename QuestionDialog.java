package com.example.mathsprout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class QuestionDialog extends Dialog {

    private EditText questionInput, optionAInput, optionBInput, optionCInput;
    private Spinner correctSpinner, categorySpinner;
    private Button saveBtn, cancelBtn, imageBtn;
    private ImageView imagePreview;

    private Uri imageUri;
    private Question existingQuestion;
    private OnQuestionSavedListener listener;

    private static final int PICK_IMAGE = 101;

    public QuestionDialog(
            @NonNull Context context,
            Question question,
            OnQuestionSavedListener listener
    ) {
        super(context);
        setContentView(R.layout.dialog_question);

        this.existingQuestion = question;
        this.listener = listener;

        bindViews();
        setupSpinners();
        loadExistingData();

        imageBtn.setOnClickListener(v -> pickImage());
        saveBtn.setOnClickListener(v -> saveQuestion());
        cancelBtn.setOnClickListener(v -> dismiss());
    }

    private void bindViews() {
        questionInput = findViewById(R.id.questionInput);
        optionAInput = findViewById(R.id.optionAInput);
        optionBInput = findViewById(R.id.optionBInput);
        optionCInput = findViewById(R.id.optionCInput);
        correctSpinner = findViewById(R.id.correctSpinner);
        categorySpinner = findViewById(R.id.categorySpinner);
        saveBtn = findViewById(R.id.saveQuestionBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        imageBtn = findViewById(R.id.pickImageBtn);
        imagePreview = findViewById(R.id.imagePreview);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> correctAdapter =
                ArrayAdapter.createFromResource(
                        getContext(),
                        R.array.correct_answers,
                        android.R.layout.simple_spinner_item
                );
        correctAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        correctSpinner.setAdapter(correctAdapter);

        ArrayAdapter<CharSequence> categoryAdapter =
                ArrayAdapter.createFromResource(
                        getContext(),
                        R.array.lesson_categories,
                        android.R.layout.simple_spinner_item
                );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void loadExistingData() {
        if (existingQuestion == null) return;

        questionInput.setText(existingQuestion.getQuestionText());
        optionAInput.setText(existingQuestion.getOptionA());
        optionBInput.setText(existingQuestion.getOptionB());
        optionCInput.setText(existingQuestion.getOptionC());

        correctSpinner.setSelection(
                existingQuestion.getCorrectAnswer().equals("A") ? 0 :
                        existingQuestion.getCorrectAnswer().equals("B") ? 1 : 2
        );

        String[] categories = getContext().getResources()
                .getStringArray(R.array.lesson_categories);
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(existingQuestion.getCategory())) {
                categorySpinner.setSelection(i);
                break;
            }
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        ((android.app.Activity) getContext())
                .startActivityForResult(intent, PICK_IMAGE);
    }

    public void setImageUri(Uri uri) {
        this.imageUri = uri;
        imagePreview.setImageURI(uri);
        imagePreview.setVisibility(View.VISIBLE);
    }

    private void saveQuestion() {
        String q = questionInput.getText().toString().trim();
        String a = optionAInput.getText().toString().trim();
        String b = optionBInput.getText().toString().trim();
        String c = optionCInput.getText().toString().trim();

        if (q.isEmpty() || a.isEmpty() || b.isEmpty() || c.isEmpty()) {
            Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String correct = correctSpinner.getSelectedItemPosition() == 0 ? "A" :
                correctSpinner.getSelectedItemPosition() == 1 ? "B" : "C";

        String category = categorySpinner.getSelectedItem().toString();

        if (imageUri != null) {
            uploadImage(q, a, b, c, correct, category);
        } else {
            listener.onSaved(
                    new Question(q, a, b, c, correct, category, null)
            );
            dismiss();
        }
    }

    private void uploadImage(
            String q, String a, String b, String c,
            String correct, String category
    ) {
        StorageReference ref = FirebaseStorage.getInstance()
                .getReference("lesson_images/" + System.currentTimeMillis());

        ref.putFile(imageUri).addOnSuccessListener(task ->
                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    listener.onSaved(
                            new Question(q, a, b, c, correct, category, uri.toString())
                    );
                    dismiss();
                })
        );
    }

    public interface OnQuestionSavedListener {
        void onSaved(Question question);
    }
}
