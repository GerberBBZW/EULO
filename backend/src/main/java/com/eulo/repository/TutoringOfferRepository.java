package com.eulo.repository;
 
import com.eulo.model.TutoringOffer;
import org.springframework.data.mongodb.repository.MongoRepository;
 
import java.util.List;
 
public interface TutoringOfferRepository extends MongoRepository<TutoringOffer, String> {
    List<TutoringOffer> findByTutorId(String tutorId);
    List<TutoringOffer> findBySubjectName(String subjectName);
    List<TutoringOffer> findByMode(String mode);
}
