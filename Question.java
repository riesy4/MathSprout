package com.example.mathsprout;

public class Question {

    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String correctAnswer;
    private String category;
    private String imageUrl;

    // Required for Firebase
    public Question() {}

    public Question(String questionText, String optionA, String optionB,
                    String optionC, String correctAnswer,
                    String category, String imageUrl) {
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.correctAnswer = correctAnswer;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    // -------- Getters --------

    public String getQuestionText() {
        return questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
