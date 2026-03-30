package com.eulo.service;
 
import com.eulo.model.Session;
import com.eulo.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
 
import java.util.List;
import java.util.Optional;
 
@Service
@RequiredArgsConstructor
public class SessionService {
 
    private final SessionRepository sessionRepository;
 
    public List<Session> findByUserId(String userId) {
        return sessionRepository.findBySeekerIdOrTutorId(userId, userId);
    }
 
    public Session save(Session session) {
        return sessionRepository.save(session);
    }
 
    public Optional<Session> updateStatus(String id, String status) {
        return sessionRepository.findById(id).map(session -> {
            session.setStatus(status);
            return sessionRepository.save(session);
        });
    }
}
