package com.eulo.repository;
 
import com.eulo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
 
import java.util.Optional;
 
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndPassword(String email, String password);
}
