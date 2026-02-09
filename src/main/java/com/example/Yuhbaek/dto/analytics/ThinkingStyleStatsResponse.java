package com.example.Yuhbaek.dto.analytics;

public class ThinkingStyleStatsResponse {

    private final int critic;
    private final int emotion;
    private final int analysis;
    private final int empathy;
    private final int creative;

    public ThinkingStyleStatsResponse(int critic, int emotion, int analysis, int empathy, int creative) {
        this.critic = critic;
        this.emotion = emotion;
        this.analysis = analysis;
        this.empathy = empathy;
        this.creative = creative;
    }

    public int getCritic() { return critic; }
    public int getEmotion() { return emotion; }
    public int getAnalysis() { return analysis; }
    public int getEmpathy() { return empathy; }
    public int getCreative() { return creative; }
}
