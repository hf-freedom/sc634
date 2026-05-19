package com.training.model;

import lombok.Data;
import java.util.List;

@Data
public class ExamQuestion {
    private Long id;
    private String question;
    private List<String> options;
    private Integer correctAnswerIndex;
    private Integer score;
}
