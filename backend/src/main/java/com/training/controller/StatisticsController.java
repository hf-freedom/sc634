package com.training.controller;

import com.training.dto.ApiResponse;
import com.training.model.Statistics;
import com.training.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;

    @GetMapping
    public ApiResponse<Statistics> getStatistics() {
        return ApiResponse.success(statisticsService.getStatistics());
    }
}
