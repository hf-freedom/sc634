package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.model.Enrollment;
import com.training.model.LearningProgress;
import com.training.service.LearningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning")
public class LearningController {
    @Autowired
    private LearningService learningService;

    @PostMapping("/enroll")
    public ApiResponse<Enrollment> enrollCourse(@RequestParam Long userId, @RequestParam Long courseId) {
        try {
            Enrollment enrollment = learningService.enrollCourse(userId, courseId);
            return enrollment != null ? ApiResponse.success("报名成功", enrollment) : ApiResponse.error("已报名或报名失败");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/progress")
    public ApiResponse<LearningProgress> getLearningProgress(@RequestParam Long userId, @RequestParam Long courseId) {
        LearningProgress progress = learningService.getLearningProgress(userId, courseId);
        return progress != null ? ApiResponse.success(progress) : ApiResponse.error("学习进度不存在");
    }

    @PostMapping("/complete-task")
    public ApiResponse<LearningProgress> completeTask(@RequestParam Long userId, @RequestParam Long courseId, @RequestParam Long taskId) {
        LearningProgress progress = learningService.completeTask(userId, courseId, taskId);
        return progress != null ? ApiResponse.success("任务完成", progress) : ApiResponse.error("操作失败");
    }

    @GetMapping("/can-take-exam")
    public ApiResponse<Boolean> canTakeExam(@RequestParam Long userId, @RequestParam Long courseId) {
        return ApiResponse.success(learningService.canTakeExam(userId, courseId));
    }

    @GetMapping("/enrollments/{userId}")
    public ApiResponse<List<Enrollment>> getEnrollmentsByUser(@PathVariable Long userId) {
        return ApiResponse.success(learningService.getEnrollmentsByUser(userId));
    }
}
