package com.eulo.model;
 
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
 
import java.util.List;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
 
    @Id
    private String id;
 
    private String name;
 
    @Indexed(unique = true)
    private String email;
 
    private String password;
 
    private String role; // student, teacher, admin
 
    private String vocationalGroup;
 
    private String avatarUrl;
 
    private String bio;
 
    private List<String> subjectsTutored;
 
    private int sessionsCompleted;
}
