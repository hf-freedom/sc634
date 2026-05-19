package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.model.User;
import com.training.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ApiResponse<User> getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
        return user != null ? ApiResponse.success(user) : ApiResponse.error("用户不存在");
    }

    @GetMapping
    public ApiResponse<List<User>> getAllUsers() {
        return ApiResponse.success(userService.getAllUsers());
    }

    @PostMapping
    public ApiResponse<User> createUser(@RequestBody User user) {
        return ApiResponse.success("创建成功", userService.createUser(user));
    }
}
