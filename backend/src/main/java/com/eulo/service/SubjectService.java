package com.eulo.service;
 
import com.eulo.model.Subject;
import com.eulo.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
 
import java.util.List;
 
@Service
@RequiredArgsConstructor
public class SubjectService {
 
    private final SubjectRepository subjectRepository;
 
    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }
}
