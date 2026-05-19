package com.training.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Enrollment {
    private Long id;
    private Long userId;
    private Long courseId;
    private LocalDateTime enrolledAt;
}
