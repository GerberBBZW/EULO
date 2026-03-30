package com.eulo.service;
 
import com.eulo.model.DashboardStats;
import com.eulo.model.Session;
import com.eulo.repository.SessionRepository;
import com.eulo.repository.SubjectRepository;
import com.eulo.repository.TutoringOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
 
import java.util.List;
import java.util.stream.Collectors;
 
@Service
@RequiredArgsConstructor
public class DashboardService {
 
    private final SessionRepository sessionRepository;
    private final TutoringOfferRepository offerRepository;
    private final SubjectRepository subjectRepository;
 
    public DashboardStats getStats(String userId) {
        // Upcoming sessions for this user (open or matched)
        List<Session> userSessions = sessionRepository.findBySeekerIdOrTutorId(userId, userId);
        long upcomingSessions = userSessions.stream()
                .filter(s -> "open".equals(s.getStatus()) || "matched".equals(s.getStatus()))
                .count();
 
        // Distinct tutors with active offers
        long availableTutors = offerRepository.findAll().stream()
                .map(o -> o.getTutorId())
                .collect(Collectors.toSet())
                .size();
 
        // Total subjects
        long subjectsOffered = subjectRepository.count();
 
        // Next upcoming session (matched) for this user as seeker
        Session upcomingSession = userSessions.stream()
                .filter(s -> "matched".equals(s.getStatus()) && userId.equals(s.getSeekerId()))
                .findFirst()
                .orElse(null);
 
        return new DashboardStats(
                (int) upcomingSessions,
                (int) availableTutors,
                (int) subjectsOffered,
                upcomingSession
        );
    }
}
