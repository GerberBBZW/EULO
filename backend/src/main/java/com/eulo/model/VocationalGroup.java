package com.eulo.model;
 
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
 
import java.util.List;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vocational_groups")
public class VocationalGroup {
 
    @Id
    private String id;
 
    private String name;
 
    private String description;
 
    private String icon;
 
    private List<Subject> subjects;
}
