package com.training.storage;

import com.training.model.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStorage {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Map<Long, Course> courses = new ConcurrentHashMap<>();
    private final Map<Long, Enrollment> enrollments = new ConcurrentHashMap<>();
    private final Map<Long, LearningProgress> learningProgresses = new ConcurrentHashMap<>();
    private final Map<Long, ExamRecord> examRecords = new ConcurrentHashMap<>();
    private final Map<Long, Certificate> certificates = new ConcurrentHashMap<>();

    private final AtomicLong userIdGenerator = new AtomicLong(1);
    private final AtomicLong courseIdGenerator = new AtomicLong(1);
    private final AtomicLong enrollmentIdGenerator = new AtomicLong(1);
    private final AtomicLong progressIdGenerator = new AtomicLong(1);
    private final AtomicLong examRecordIdGenerator = new AtomicLong(1);
    private final AtomicLong certificateIdGenerator = new AtomicLong(1);

    public InMemoryStorage() {
        initDefaultData();
    }

    private void initDefaultData() {
        User admin = new User();
        admin.setId(userIdGenerator.incrementAndGet());
        admin.setUsername("admin");
        admin.setName("管理员");
        admin.setRole("ADMIN");
        admin.setEmail("admin@example.com");
        admin.setCreatedAt(LocalDateTime.now());
        users.put(admin.getId(), admin);

        User employee = new User();
        employee.setId(userIdGenerator.incrementAndGet());
        employee.setUsername("employee");
        employee.setName("张三");
        employee.setRole("EMPLOYEE");
        employee.setEmail("zhangsan@example.com");
        employee.setCreatedAt(LocalDateTime.now());
        users.put(employee.getId(), employee);
    }

    public User getUser(Long id) { return users.get(id); }
    public List<User> getAllUsers() { return new ArrayList<>(users.values()); }
    public User saveUser(User user) {
        if (user.getId() == null) {
            user.setId(userIdGenerator.incrementAndGet());
        }
        users.put(user.getId(), user);
        return user;
    }

    public Course getCourse(Long id) { return courses.get(id); }
    public List<Course> getAllCourses() { return new ArrayList<>(courses.values()); }
    public Course saveCourse(Course course) {
        if (course.getId() == null) {
            course.setId(courseIdGenerator.incrementAndGet());
        }
        courses.put(course.getId(), course);
        return course;
    }

    public Enrollment getEnrollment(Long id) { return enrollments.get(id); }
    public List<Enrollment> getEnrollmentsByUser(Long userId) {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment e : enrollments.values()) {
            if (e.getUserId().equals(userId)) {
                result.add(e);
            }
        }
        return result;
    }
    public Enrollment getEnrollment(Long userId, Long courseId) {
        for (Enrollment e : enrollments.values()) {
            if (e.getUserId().equals(userId) && e.getCourseId().equals(courseId)) {
                return e;
            }
        }
        return null;
    }
    public Enrollment saveEnrollment(Enrollment enrollment) {
        if (enrollment.getId() == null) {
            enrollment.setId(enrollmentIdGenerator.incrementAndGet());
        }
        enrollments.put(enrollment.getId(), enrollment);
        return enrollment;
    }

    public LearningProgress getLearningProgress(Long id) { return learningProgresses.get(id); }
    public LearningProgress getLearningProgress(Long userId, Long courseId) {
        for (LearningProgress p : learningProgresses.values()) {
            if (p.getUserId().equals(userId) && p.getCourseId().equals(courseId)) {
                return p;
            }
        }
        return null;
    }
    public LearningProgress saveLearningProgress(LearningProgress progress) {
        if (progress.getId() == null) {
            progress.setId(progressIdGenerator.incrementAndGet());
        }
        learningProgresses.put(progress.getId(), progress);
        return progress;
    }

    public ExamRecord getExamRecord(Long id) { return examRecords.get(id); }
    public List<ExamRecord> getExamRecordsByUserAndCourse(Long userId, Long courseId) {
        List<ExamRecord> result = new ArrayList<>();
        for (ExamRecord e : examRecords.values()) {
            if (e.getUserId().equals(userId) && e.getCourseId().equals(courseId)) {
                result.add(e);
            }
        }
        result.sort(Comparator.comparing(ExamRecord::getAttemptNumber).reversed());
        return result;
    }
    public ExamRecord saveExamRecord(ExamRecord record) {
        if (record.getId() == null) {
            record.setId(examRecordIdGenerator.incrementAndGet());
        }
        examRecords.put(record.getId(), record);
        return record;
    }

    public Certificate getCertificate(Long id) { return certificates.get(id); }
    public List<Certificate> getCertificatesByUser(Long userId) {
        List<Certificate> result = new ArrayList<>();
        for (Certificate c : certificates.values()) {
            if (c.getUserId().equals(userId)) {
                result.add(c);
            }
        }
        return result;
    }
    public List<Certificate> getAllCertificates() {
        return new ArrayList<>(certificates.values());
    }
    public Certificate saveCertificate(Certificate certificate) {
        if (certificate.getId() == null) {
            certificate.setId(certificateIdGenerator.incrementAndGet());
        }
        certificates.put(certificate.getId(), certificate);
        return certificate;
    }

    public List<LearningProgress> getAllLearningProgresses() {
        return new ArrayList<>(learningProgresses.values());
    }
}
