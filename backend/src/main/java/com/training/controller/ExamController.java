package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.dto.ExamSubmitRequest;
import com.training.model.ExamRecord;
import com.training.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam")
public class ExamController {
    @Autowired
    private ExamService examService;

    @PostMapping("/start")
    public ApiResponse<ExamRecord> startExam(@RequestParam Long userId, @RequestParam Long courseId) {
        ExamRecord record = examService.startExam(userId, courseId);
        return record != null ? ApiResponse.success("考试开始", record) : ApiResponse.error("无法参加考试，请先完成学习或补考次数已用完");
    }

    @PostMapping("/submit")
    public ApiResponse<ExamRecord> submitExam(@RequestParam Long examRecordId, @RequestBody ExamSubmitRequest request) {
        ExamRecord record = examService.submitExam(examRecordId, request.getAnswers());
        return record != null ? ApiResponse.success("提交成功", record) : ApiResponse.error("提交失败");
    }

    @GetMapping("/can-retake")
    public ApiResponse<Boolean> canRetakeExam(@RequestParam Long userId, @RequestParam Long courseId) {
        return ApiResponse.success(examService.canRetakeExam(userId, courseId));
    }

    @GetMapping("/needs-relearn")
    public ApiResponse<Boolean> needsRelearn(@RequestParam Long userId, @RequestParam Long courseId) {
        return ApiResponse.success(examService.needsRelearn(userId, courseId));
    }

    @GetMapping("/records")
    public ApiResponse<List<ExamRecord>> getExamRecords(@RequestParam Long userId, @RequestParam Long courseId) {
        return ApiResponse.success(examService.getExamRecords(userId, courseId));
    }
}
