package com.training.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Certificate {
    private Long id;
    private String certificateNumber;
    private Long userId;
    private Long courseId;
    private Long examRecordId;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;
    private boolean expiredReminderSent;
}
