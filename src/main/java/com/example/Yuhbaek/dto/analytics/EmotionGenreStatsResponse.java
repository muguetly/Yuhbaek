package com.example.Yuhbaek.dto.analytics;

import java.util.List;

public class EmotionGenreStatsResponse {

    private final String mode;
    private final Integer emotionId; // mode에 따라 null 가능
    private final String genre;      // mode에 따라 null 가능
    private final List<DonutSliceDto> slices;

    public EmotionGenreStatsResponse(String mode, Integer emotionId, String genre, List<DonutSliceDto> slices) {
        this.mode = mode;
        this.emotionId = emotionId;
        this.genre = genre;
        this.slices = slices;
    }

    public String getMode() { return mode; }
    public Integer getEmotionId() { return emotionId; }
    public String getGenre() { return genre; }
    public List<DonutSliceDto> getSlices() { return slices; }
}
