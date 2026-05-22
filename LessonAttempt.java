package com.example.mathsprout;

public class LessonAttempt {
    private String lessonName;
    private int score;
    private int total;
    private String date;

    public LessonAttempt() {} // Required for Firebase

    public LessonAttempt(String lessonName, int score, int total, String date) {
        this.lessonName = lessonName;
        this.score = score;
        this.total = total;
        this.date = date;
    }

    public String getLessonName() {
        return (lessonName != null && !lessonName.isEmpty()) ? lessonName : "Untitled Lesson";
    }

    public int getScore() { return score; }
    public int getTotal() { return total; }
    public String getDate() { return date; }
}