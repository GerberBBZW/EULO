package com.eulo.service;

import com.eulo.model.DashboardStats;
import com.eulo.model.Session;
import com.eulo.repository.SessionRepository;
import com.eulo.repository.SubjectRepository;
import com.eulo.repository.TutoringOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SessionRepository sessionRepository;
    private final TutoringOfferRepository offerRepository;
    private final SubjectRepository subjectRepository;

    public DashboardStats getStats(String userId) {
        List<Session> userSessions = sessionRepository.findBySeekerIdOrTutorId(userId, userId);
        Instant now = Instant.now();

        // Upcoming = open or matched, AND in the future
        long upcomingSessions = userSessions.stream()
                .filter(s -> isActiveStatus(s.getStatus()) && isFuture(s.getDate(), now))
                .count();

        // Distinct tutors with active offers
        long availableTutors = offerRepository.findAll().stream()
                .map(o -> o.getTutorId())
                .collect(Collectors.toSet())
                .size();

        // Total subjects
        long subjectsOffered = subjectRepository.count();

        // Next upcoming matched session for this user as seeker, sorted by date
        Session upcomingSession = userSessions.stream()
                .filter(s -> "matched".equals(s.getStatus())
                        && userId.equals(s.getSeekerId())
                        && isFuture(s.getDate(), now))
                .min(Comparator.comparing(s -> parseDate(s.getDate())))
                .orElse(null);

        return new DashboardStats(
                (int) upcomingSessions,
                (int) availableTutors,
                (int) subjectsOffered,
                upcomingSession
        );
    }

    private boolean isActiveStatus(String status) {
        return "open".equals(status) || "matched".equals(status);
    }

    private boolean isFuture(String dateStr, Instant now) {
        try {
            return Instant.parse(dateStr).isAfter(now);
        } catch (Exception e) {
            return false;
        }
    }

    private Instant parseDate(String dateStr) {
        try {
            return Instant.parse(dateStr);
        } catch (Exception e) {
            return Instant.MAX;
        }
    }
}
