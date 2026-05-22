package com.example.mathsprout;

public class QuestionAttempt {
    private String question;
    private String correctAnswer;
    private String childAnswer;
    private boolean isCorrect;

    public QuestionAttempt() {}

    public QuestionAttempt(String question, String correctAnswer, String childAnswer, boolean isCorrect) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.childAnswer = childAnswer;
        this.isCorrect = isCorrect;
    }

    public String getQuestion() { return question; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getChildAnswer() { return childAnswer; }
    public boolean isCorrect() { return isCorrect; }
}
