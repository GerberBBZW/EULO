package com.eulo.repository;
 
import com.eulo.model.Subject;
import org.springframework.data.mongodb.repository.MongoRepository;
 
public interface SubjectRepository extends MongoRepository<Subject, String> {
}
