package com.eulo.service;
 
import com.eulo.model.VocationalGroup;
import com.eulo.repository.VocationalGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
 
import java.util.List;
 
@Service
@RequiredArgsConstructor
public class VocationalGroupService {
 
    private final VocationalGroupRepository vocationalGroupRepository;
 
    public List<VocationalGroup> findAll() {
        return vocationalGroupRepository.findAll();
    }
}
