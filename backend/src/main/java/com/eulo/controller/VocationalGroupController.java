package com.eulo.controller;
 
import com.eulo.model.VocationalGroup;
import com.eulo.service.VocationalGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController
@RequestMapping("/api/vocational-groups")
@RequiredArgsConstructor
public class VocationalGroupController {
 
    private final VocationalGroupService vocationalGroupService;
 
    @GetMapping
    public List<VocationalGroup> getVocationalGroups() {
        return vocationalGroupService.findAll();
    }
}
