package com.example.mathsprout;

public class QuizRecord {
    public int score;
    public int stars;
    public long answeredAt;
    public String quizTitle;

    public QuizRecord() {} // Required for Firebase

    public QuizRecord(int score, int stars, long answeredAt, String quizTitle) {
        this.score = score;
        this.stars = stars;
        this.answeredAt = answeredAt;
        this.quizTitle = quizTitle;
    }
}