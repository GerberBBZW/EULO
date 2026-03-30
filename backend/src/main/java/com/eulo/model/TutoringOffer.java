package com.eulo.model;
 
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tutoring_offers")
public class TutoringOffer {
 
    @Id
    private String id;
 
    private String tutorId;
 
    private String tutorName;
 
    private String tutorAvatar;
 
    private String subjectId;
 
    private String subjectName;
 
    private String mode; // online, onsite, both
 
    private String description;
 
    private String availability;
}
