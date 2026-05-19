package com.training.service;

import com.training.model.*;
import com.training.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class CertificateService {
    @Autowired
    private InMemoryStorage storage;

    public Certificate createCertificate(Long userId, Long courseId, Long examRecordId) {
        Course course = storage.getCourse(courseId);
        ExamRule rule = course.getExamRule();
        Certificate certificate = new Certificate();
        certificate.setCertificateNumber(generateCertificateNumber());
        certificate.setUserId(userId);
        certificate.setCourseId(courseId);
        certificate.setExamRecordId(examRecordId);
        certificate.setIssuedAt(LocalDateTime.now());
        certificate.setExpiredAt(LocalDateTime.now().plusDays(rule.getCertificateValidityDays()));
        certificate.setExpiredReminderSent(false);
        return storage.saveCertificate(certificate);
    }

    private String generateCertificateNumber() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "CERT-" + dateStr + "-" + uuid;
    }

    public List<Certificate> getCertificatesByUser(Long userId) {
        return storage.getCertificatesByUser(userId);
    }

    public Certificate getCertificate(Long id) {
        return storage.getCertificate(id);
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void checkExpiringCertificates() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderThreshold = now.plusDays(30);
        for (Certificate cert : storage.getAllCertificates()) {
            if (!cert.isExpiredReminderSent() && 
                cert.getExpiredAt().isBefore(reminderThreshold) && 
                cert.getExpiredAt().isAfter(now)) {
                System.out.println("提醒：证书 " + cert.getCertificateNumber() + " 将在30天内过期，请尽快重新认证！");
                cert.setExpiredReminderSent(true);
                storage.saveCertificate(cert);
            }
        }
    }
}
