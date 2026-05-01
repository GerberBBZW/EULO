package com.eulo.service;

import com.eulo.model.Session;
import com.eulo.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    public Optional<Session> findById(String id) {
        return sessionRepository.findById(id);
    }

    public List<Session> findByUserId(String userId) {
        return sessionRepository.findBySeekerIdOrTutorId(userId, userId);
    }

    public Session save(Session session) {
        Session saved = sessionRepository.save(session);
        log.info("EVENT=BOOKING_CREATED tutorId={} studentId={} subject={}",
                saved.getTutorId(), saved.getSeekerId(), saved.getSubjectName());
        return saved;
    }

    public Optional<Session> updateStatus(String id, String status) {
        return sessionRepository.findById(id).map(session -> {
            String oldStatus = session.getStatus();
            session.setStatus(status);
            Session saved = sessionRepository.save(session);
            if ("cancelled".equals(status)) {
                log.info("EVENT=BOOKING_CANCELLED reason=user-action bookingId={}", id);
            } else {
                log.info("EVENT=BOOKING_STATUS_CHANGED bookingId={} from={} to={}", id, oldStatus, status);
            }
            return saved;
        });
    }
}
