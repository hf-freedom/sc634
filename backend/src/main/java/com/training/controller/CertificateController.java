package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.model.Certificate;
import com.training.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {
    @Autowired
    private CertificateService certificateService;

    @GetMapping("/{id}")
    public ApiResponse<Certificate> getCertificate(@PathVariable Long id) {
        Certificate cert = certificateService.getCertificate(id);
        return cert != null ? ApiResponse.success(cert) : ApiResponse.error("证书不存在");
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Certificate>> getCertificatesByUser(@PathVariable Long userId) {
        return ApiResponse.success(certificateService.getCertificatesByUser(userId));
    }
}
