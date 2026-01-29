package com.example.Yuhbaek.entity.SignUp;

public enum ReadingStyle {
    LIGHT_READ("가볍게 읽는"),
    EASY_READ("술술 읽히는"),
    SLOW_READ("천천히 곱씹는"),
    SHORT_CONTENT("짧은 글 위주"),
    DEEP_FOCUS_BOOK("한 권에 몰입"),
    DEEP_FOCUS_SENTENCE("문장 하나에 몰입");

    private final String description;

    ReadingStyle(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}