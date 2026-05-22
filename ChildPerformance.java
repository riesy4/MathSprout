package com.example.mathsprout;

public class ChildPerformance {
    private String uid;
    private String name;
    private int score;
    private int total;
    private String reward;

    public ChildPerformance() {}

    public ChildPerformance(String uid, String name, int score, int total, String reward) {
        this.uid = uid;
        this.name = name;
        this.score = score;
        this.total = total;
        this.reward = reward;
    }

    public String getUid() { return uid; }
    public String getName() { return name; }
    public int getScore() { return score; }
    public int getTotal() { return total; }
    public String getReward() { return reward; }
}
