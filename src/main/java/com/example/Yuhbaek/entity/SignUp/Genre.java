package com.example.Yuhbaek.entity.SignUp;

public enum Genre {
    // 소설
    ROMANCE("로맨스"),
    THRILLER("스릴러"),
    MYSTERY("미스터리"),
    FANTASY("판타지"),
    SF("SF"),
    COMING_OF_AGE("성장소설"),
    HISTORICAL_FICTION("역사소설"),
    HUMAN_DRAMA("휴먼드라마"),

    // 비소설
    ESSAY("에세이"),
    HUMANITIES("인문"),
    SOCIAL("사회"),
    PHILOSOPHY("철학"),
    PSYCHOLOGY("심리"),
    SELF_DEVELOPMENT("자기계발"),
    BUSINESS("경제경영"),
    SCIENCE("과학");

    private final String description;

    Genre(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}