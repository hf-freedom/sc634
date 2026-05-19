package com.training.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamRecord {
    private Long id;
    private Long userId;
    private Long courseId;
    private Integer attemptNumber;
    private List<Integer> userAnswers;
    private Integer score;
    private boolean passed;
    private LocalDateTime startTime;
    private LocalDateTime submitTime;
    private boolean isRetake;
}
