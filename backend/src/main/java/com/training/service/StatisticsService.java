package com.training.service;

import com.training.model.*;
import com.training.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatisticsService {
    @Autowired
    private InMemoryStorage storage;

    public Statistics getStatistics() {
        Statistics stats = new Statistics();
        List<Course> courses = storage.getAllCourses();
        List<User> users = storage.getAllUsers();
        List<LearningProgress> progresses = storage.getAllLearningProgresses();
        List<Certificate> certificates = storage.getAllCertificates();
        stats.setTotalCourses((long) courses.size());
        stats.setTotalUsers((long) users.size());
        long completedProgresses = progresses.stream().filter(LearningProgress::isCompleted).count();
        stats.setCourseCompletionRate(progresses.isEmpty() ? 0.0 : (double) completedProgresses / progresses.size() * 100);
        long totalExams = 0;
        long passedExams = 0;
        for (User user : users) {
            for (Course course : courses) {
                List<ExamRecord> records = storage.getExamRecordsByUserAndCourse(user.getId(), course.getId());
                if (!records.isEmpty()) {
                    totalExams++;
                    if (records.stream().anyMatch(ExamRecord::isPassed)) {
                        passedExams++;
                    }
                }
            }
        }
        stats.setExamPassRate(totalExams == 0 ? 0.0 : (double) passedExams / totalExams * 100);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiringThreshold = now.plusDays(30);
        long expiringCount = certificates.stream()
                .filter(c -> c.getExpiredAt().isAfter(now) && c.getExpiredAt().isBefore(expiringThreshold))
                .count();
        long expiredCount = certificates.stream()
                .filter(c -> c.getExpiredAt().isBefore(now))
                .count();
        stats.setExpiringCertificatesCount(expiringCount);
        stats.setExpiredCertificatesCount(expiredCount);
        return stats;
    }
}
