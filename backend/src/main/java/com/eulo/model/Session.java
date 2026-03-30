package com.eulo.model;
 
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sessions")
public class Session {
 
    @Id
    private String id;
 
    private String seekerId;
 
    private String seekerName;
 
    private String tutorId;
 
    private String tutorName;
 
    private String subjectId;
 
    private String subjectName;
 
    private String status; // open, matched, completed, cancelled
 
    private String date; // ISO-8601 string
 
    private String mode; // online, onsite, both
 
    private String notes;
}
