package com.example.Yuhbaek.dto.analytics;

public class HourlyActivityDto {

    private final int hour;
    private final long messageCount;
    private final long charCount;
    private final double participationScore;

    public HourlyActivityDto(int hour, long messageCount, long charCount) {
        this.hour = hour;
        this.messageCount = messageCount;
        this.charCount = charCount;
        // 🔹 참여도 공식 (지금은 단순, 나중에 조절 가능)
        this.participationScore = messageCount + (charCount / 100.0);
    }

    public int getHour() { return hour; }
    public long getMessageCount() { return messageCount; }
    public long getCharCount() { return charCount; }
    public double getParticipationScore() { return participationScore; }
}
