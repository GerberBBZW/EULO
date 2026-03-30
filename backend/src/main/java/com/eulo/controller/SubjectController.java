package com.eulo.controller;
 
import com.eulo.model.Subject;
import com.eulo.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {
 
    private final SubjectService subjectService;
 
    @GetMapping
    public List<Subject> getSubjects() {
        return subjectService.findAll();
    }
}
