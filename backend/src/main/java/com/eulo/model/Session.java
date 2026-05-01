package com.eulo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "seekerId darf nicht leer sein")
    private String seekerId;

    @NotBlank(message = "seekerName darf nicht leer sein")
    private String seekerName;

    @NotBlank(message = "tutorId darf nicht leer sein")
    private String tutorId;

    @NotBlank(message = "tutorName darf nicht leer sein")
    private String tutorName;

    @NotBlank(message = "subjectId darf nicht leer sein")
    private String subjectId;

    @NotBlank(message = "subjectName darf nicht leer sein")
    private String subjectName;

    private String status; // open, matched, completed, cancelled

    @NotBlank(message = "date darf nicht leer sein")
    private String date; // ISO-8601 string

    @NotBlank(message = "mode darf nicht leer sein")
    private String mode; // online, onsite, both

    @Size(max = 500, message = "Notizen dürfen maximal 500 Zeichen lang sein")
    private String notes;
}
