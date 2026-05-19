package com.training.dto;

import lombok.Data;
import java.util.List;

@Data
public class ExamSubmitRequest {
    private Long userId;
    private Long courseId;
    private List<Integer> answers;
}
