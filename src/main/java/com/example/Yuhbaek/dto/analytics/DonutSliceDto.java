package com.example.Yuhbaek.dto.analytics;

public class DonutSliceDto {
    private final String label;
    private final long value;

    public DonutSliceDto(String label, long value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() { return label; }
    public long getValue() { return value; }
}
