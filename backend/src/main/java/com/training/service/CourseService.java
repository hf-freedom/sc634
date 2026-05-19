package com.training.service;

import com.training.model.*;
import com.training.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CourseService {
    @Autowired
    private InMemoryStorage storage;

    public Course getCourse(Long id) {
        return storage.getCourse(id);
    }

    public List<Course> getAllCourses() {
        return storage.getAllCourses();
    }

    public Course createCourse(Course course) {
        validateCourse(course);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        return storage.saveCourse(course);
    }

    public Course updateCourse(Long id, Course course) {
        Course existing = storage.getCourse(id);
        if (existing == null) {
            return null;
        }
        validateCourse(course);
        course.setId(id);
        course.setCreatedAt(existing.getCreatedAt());
        course.setUpdatedAt(LocalDateTime.now());
        return storage.saveCourse(course);
    }

    private void validateCourse(Course course) {
        if (course.getTitle() == null || course.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("课程名称不能为空");
        }
        if (course.getLearningTasks() == null || course.getLearningTasks().isEmpty()) {
            throw new IllegalArgumentException("至少需要一个学习任务");
        }
        for (LearningTask task : course.getLearningTasks()) {
            if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("学习任务标题不能为空");
            }
        }
        if (course.getExamRule() == null) {
            throw new IllegalArgumentException("考试规则不能为空");
        }
        if (course.getExamRule().getPassingScore() == null || course.getExamRule().getPassingScore() <= 0) {
            throw new IllegalArgumentException("及格分数必须大于0");
        }
        if (course.getExamQuestions() == null || course.getExamQuestions().isEmpty()) {
            throw new IllegalArgumentException("至少需要一道考试题目");
        }
        for (ExamQuestion question : course.getExamQuestions()) {
            if (question.getQuestion() == null || question.getQuestion().trim().isEmpty()) {
                throw new IllegalArgumentException("考试题目的题干不能为空");
            }
        }
        int totalScore = course.getExamQuestions().stream().mapToInt(q -> q.getScore() != null ? q.getScore() : 0).sum();
        if (totalScore < course.getExamRule().getPassingScore()) {
            throw new IllegalArgumentException("题目总分(" + totalScore + ")不能小于及格分数(" + course.getExamRule().getPassingScore() + ")");
        }
    }

    public List<ExamQuestion> getExamQuestions(Long courseId) {
        Course course = storage.getCourse(courseId);
        return course != null ? course.getExamQuestions() : new ArrayList<>();
    }

    public ExamRule getExamRule(Long courseId) {
        Course course = storage.getCourse(courseId);
        return course != null ? course.getExamRule() : null;
    }
}
