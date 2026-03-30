package com.eulo.controller;
 
import com.eulo.model.DashboardStats;
import com.eulo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
 
    private final DashboardService dashboardService;
 
    @GetMapping("/stats")
    public DashboardStats getStats(@RequestParam String userId) {
        return dashboardService.getStats(userId);
    }
}
