package com.eulo.controller;
 
import com.eulo.model.DashboardStats;
import com.eulo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * IDOR guard: users may only query their own dashboard stats.
     */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getStats(
            @RequestParam String userId,
            Authentication auth) {
        String me = (String) auth.getPrincipal();
        if (!me.equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(dashboardService.getStats(userId));
    }
}
