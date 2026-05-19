package com.training.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class LearningProgress {
    private Long id;
    private Long userId;
    private Long courseId;
    private List<Long> completedTaskIds;
    private Integer totalTasks;
    private boolean completed;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
