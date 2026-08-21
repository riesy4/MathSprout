package com.example.mathsprout;

public class PerformanceItem {
    private String childUid;
    private int score;
    private int total;
    private int percentage;

    public PerformanceItem(String childUid, int score, int total, int percentage) {
        this.childUid = childUid;
        this.score = score;
        this.total = total;
        this.percentage = percentage;
    }

    public String getChildUid() { return childUid; }
    public int getScore() { return score; }
    public int getTotal() { return total; }
    public int getPercentage() { return percentage; }
}


