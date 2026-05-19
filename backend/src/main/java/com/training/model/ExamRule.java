package com.training.model;

import lombok.Data;

@Data
public class ExamRule {
    private Long id;
    private Integer passingScore;
    private Integer totalQuestions;
    private Integer timeLimitMinutes;
    private Integer maxRetakeCount;
    private Integer certificateValidityDays;
}
