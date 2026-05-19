package com.training.service;

import com.training.model.*;
import com.training.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LearningService {
    @Autowired
    private InMemoryStorage storage;

    public Enrollment enrollCourse(Long userId, Long courseId) {
        if (storage.getEnrollment(userId, courseId) != null) {
            return null;
        }
        Course course = storage.getCourse(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        if (course.getLearningTasks() == null || course.getLearningTasks().isEmpty()) {
            throw new IllegalArgumentException("该课程暂无学习任务，无法报名");
        }
        if (course.getExamQuestions() == null || course.getExamQuestions().isEmpty()) {
            throw new IllegalArgumentException("该课程暂无考试题目，无法报名");
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(userId);
        enrollment.setCourseId(courseId);
        enrollment.setEnrolledAt(LocalDateTime.now());
        Enrollment saved = storage.saveEnrollment(enrollment);
        initLearningProgress(userId, courseId);
        return saved;
    }

    private void initLearningProgress(Long userId, Long courseId) {
        Course course = storage.getCourse(courseId);
        LearningProgress progress = new LearningProgress();
        progress.setUserId(userId);
        progress.setCourseId(courseId);
        progress.setCompletedTaskIds(new ArrayList<>());
        progress.setTotalTasks(course.getLearningTasks() != null ? course.getLearningTasks().size() : 0);
        progress.setCompleted(false);
        progress.setStartedAt(LocalDateTime.now());
        storage.saveLearningProgress(progress);
    }

    public LearningProgress getLearningProgress(Long userId, Long courseId) {
        return storage.getLearningProgress(userId, courseId);
    }

    public LearningProgress completeTask(Long userId, Long courseId, Long taskId) {
        LearningProgress progress = storage.getLearningProgress(userId, courseId);
        if (progress == null) {
            return null;
        }
        Course course = storage.getCourse(courseId);
        if (course == null || course.getLearningTasks() == null || course.getLearningTasks().isEmpty()) {
            return progress;
        }
        if (!progress.getCompletedTaskIds().contains(taskId)) {
            progress.getCompletedTaskIds().add(taskId);
        }
        int totalTasks = course.getLearningTasks().size();
        if (progress.getCompletedTaskIds().size() >= totalTasks) {
            progress.setCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
        }
        return storage.saveLearningProgress(progress);
    }

    public boolean canTakeExam(Long userId, Long courseId) {
        LearningProgress progress = storage.getLearningProgress(userId, courseId);
        if (progress == null) {
            return false;
        }
        Course course = storage.getCourse(courseId);
        if (course == null) {
            return false;
        }
        if (course.getLearningTasks() == null || course.getLearningTasks().isEmpty()) {
            return false;
        }
        return progress.isCompleted();
    }

    public List<Enrollment> getEnrollmentsByUser(Long userId) {
        return storage.getEnrollmentsByUser(userId);
    }
}
