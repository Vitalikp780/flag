package com.example.flag.models;

public class UserStats {
    private int count = 0;
    private float totalScore = 0;

    public UserStats() {
    }

    public void addMessage(float score) {
        totalScore += score;
        count++;
    }

    public int getCount() {
        return count;
    }

    public float getAverageScore() {
        return count > 0 ? totalScore / count : 0;
    }
}
