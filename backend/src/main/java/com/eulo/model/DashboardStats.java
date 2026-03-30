package com.eulo.model;
 
import lombok.Data;
import lombok.AllArgsConstructor;
 
@Data
@AllArgsConstructor
public class DashboardStats {
    private int upcomingSessions;
    private int availableTutors;
    private int subjectsOffered;
    private Session upcomingSession;
}
