package com.training.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Course {
    private Long id;
    private String title;
    private String description;
    private List<LearningTask> learningTasks;
    private ExamRule examRule;
    private List<ExamQuestion> examQuestions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
