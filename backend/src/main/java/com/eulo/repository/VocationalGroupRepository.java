package com.eulo.repository;
 
import com.eulo.model.VocationalGroup;
import org.springframework.data.mongodb.repository.MongoRepository;
 
public interface VocationalGroupRepository extends MongoRepository<VocationalGroup, String> {
}
