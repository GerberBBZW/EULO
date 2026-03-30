package com.eulo.repository;
 
import com.eulo.model.Session;
import org.springframework.data.mongodb.repository.MongoRepository;
 
import java.util.List;
 
public interface SessionRepository extends MongoRepository<Session, String> {
    List<Session> findBySeekerId(String seekerId);
    List<Session> findByTutorId(String tutorId);
    List<Session> findBySeekerIdOrTutorId(String seekerId, String tutorId);
    List<Session> findBySeekerIdAndStatus(String seekerId, String status);
    List<Session> findByTutorIdAndStatus(String tutorId, String status);
}
