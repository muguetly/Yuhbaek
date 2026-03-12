package com.example.Yuhbaek.entity.analytics;

import java.util.Arrays;

public enum EmotionType {
    EXCITED(1, "신남"),
    HAPPY(2, "행복"),
    CALM(3, "평온"),
    ANXIOUS(4, "불안"),
    SAD(5, "슬픔"),
    ANGRY(6, "화남");

    private final int id;
    private final String label;

    EmotionType(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public static EmotionType fromId(int id) {
        return Arrays.stream(values())
                .filter(e -> e.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 emotionId입니다: " + id));
    }

    public static boolean isValid(Integer id) {
        if (id == null) return false;
        return Arrays.stream(values()).anyMatch(e -> e.id == id);
    }
}