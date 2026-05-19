package com.training.service;

import com.training.model.*;
import com.training.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExamService {
    @Autowired
    private InMemoryStorage storage;

    @Autowired
    private LearningService learningService;

    @Autowired
    private CertificateService certificateService;

    public ExamRecord startExam(Long userId, Long courseId) {
        if (!learningService.canTakeExam(userId, courseId)) {
            return null;
        }
        if (!canRetakeExam(userId, courseId)) {
            return null;
        }
        ExamRecord record = new ExamRecord();
        record.setUserId(userId);
        record.setCourseId(courseId);
        List<ExamRecord> previousRecords = storage.getExamRecordsByUserAndCourse(userId, courseId);
        record.setAttemptNumber(previousRecords.size() + 1);
        record.setStartTime(LocalDateTime.now());
        record.setRetake(previousRecords.size() > 0);
        return storage.saveExamRecord(record);
    }

    public ExamRecord submitExam(Long examRecordId, List<Integer> answers) {
        ExamRecord record = storage.getExamRecord(examRecordId);
        if (record == null || record.getSubmitTime() != null) {
            return null;
        }
        Course course = storage.getCourse(record.getCourseId());
        ExamRule rule = course.getExamRule();
        List<ExamQuestion> questions = course.getExamQuestions();
        record.setUserAnswers(answers);
        int score = 0;
        for (int i = 0; i < questions.size() && i < answers.size(); i++) {
            if (questions.get(i).getCorrectAnswerIndex().equals(answers.get(i))) {
                score += questions.get(i).getScore();
            }
        }
        record.setScore(score);
        record.setPassed(score >= rule.getPassingScore());
        record.setSubmitTime(LocalDateTime.now());
        ExamRecord saved = storage.saveExamRecord(record);
        if (record.isPassed()) {
            certificateService.createCertificate(record.getUserId(), record.getCourseId(), record.getId());
        }
        return saved;
    }

    public boolean canRetakeExam(Long userId, Long courseId) {
        Course course = storage.getCourse(courseId);
        if (course == null) {
            return false;
        }
        ExamRule rule = course.getExamRule();
        List<ExamRecord> records = storage.getExamRecordsByUserAndCourse(userId, courseId);
        if (records.isEmpty()) {
            return true;
        }
        ExamRecord lastRecord = records.get(0);
        if (lastRecord.isPassed()) {
            return false;
        }
        return lastRecord.getAttemptNumber() < rule.getMaxRetakeCount() + 1;
    }

    public boolean needsRelearn(Long userId, Long courseId) {
        Course course = storage.getCourse(courseId);
        if (course == null) {
            return false;
        }
        ExamRule rule = course.getExamRule();
        List<ExamRecord> records = storage.getExamRecordsByUserAndCourse(userId, courseId);
        if (records.isEmpty()) {
            return false;
        }
        ExamRecord lastRecord = records.get(0);
        return lastRecord.getAttemptNumber() >= rule.getMaxRetakeCount() + 1 && !lastRecord.isPassed();
    }

    public List<ExamRecord> getExamRecords(Long userId, Long courseId) {
        return storage.getExamRecordsByUserAndCourse(userId, courseId);
    }
}
