package com.training.model;

import lombok.Data;

@Data
public class Statistics {
    private Long totalCourses;
    private Long totalUsers;
    private Double courseCompletionRate;
    private Double examPassRate;
    private Long expiringCertificatesCount;
    private Long expiredCertificatesCount;
}
