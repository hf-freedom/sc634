package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.model.Course;
import com.training.model.ExamQuestion;
import com.training.model.ExamRule;
import com.training.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping("/{id}")
    public ApiResponse<Course> getCourse(@PathVariable Long id) {
        Course course = courseService.getCourse(id);
        return course != null ? ApiResponse.success(course) : ApiResponse.error("课程不存在");
    }

    @GetMapping
    public ApiResponse<List<Course>> getAllCourses() {
        return ApiResponse.success(courseService.getAllCourses());
    }

    @PostMapping
    public ApiResponse<Course> createCourse(@RequestBody Course course) {
        try {
            return ApiResponse.success("创建成功", courseService.createCourse(course));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<Course> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        try {
            Course updated = courseService.updateCourse(id, course);
            return updated != null ? ApiResponse.success("更新成功", updated) : ApiResponse.error("课程不存在");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{id}/questions")
    public ApiResponse<List<ExamQuestion>> getExamQuestions(@PathVariable Long id) {
        return ApiResponse.success(courseService.getExamQuestions(id));
    }

    @GetMapping("/{id}/rule")
    public ApiResponse<ExamRule> getExamRule(@PathVariable Long id) {
        ExamRule rule = courseService.getExamRule(id);
        return rule != null ? ApiResponse.success(rule) : ApiResponse.error("考试规则不存在");
    }
}
