package com.example.mathsprout;

public class Lesson {
    public String lessonName;
    public int questionCount;
    public String difficulty;


    public Lesson() {
    }

    public Lesson(String lessonName, int questionCount, String difficulty) {
        this.lessonName = lessonName;
        this.questionCount = questionCount;
        this.difficulty = difficulty;
    }

    public String getLessonName() { return lessonName; }
    public void setLessonName(String lessonName) { this.lessonName = lessonName; }

    public int getQuestionCount() { return questionCount; }
    public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}
