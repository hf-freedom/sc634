package com.training.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String name;
    private String role;
    private String email;
    private LocalDateTime createdAt;
}
