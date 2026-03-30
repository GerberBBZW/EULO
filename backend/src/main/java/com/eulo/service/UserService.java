package com.eulo.service;
 
import com.eulo.model.User;
import com.eulo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
 
import java.util.Optional;
 
@Service
@RequiredArgsConstructor
public class UserService {
 
    private final UserRepository userRepository;
 
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }
 
    public Optional<User> authenticate(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }
 
    public User save(User user) {
        return userRepository.save(user);
    }
}
