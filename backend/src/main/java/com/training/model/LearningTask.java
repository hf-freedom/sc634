package com.training.model;

import lombok.Data;

@Data
public class LearningTask {
    private Long id;
    private String title;
    private String content;
    private Integer durationMinutes;
    private Integer orderIndex;
}
