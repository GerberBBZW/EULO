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
@Document(collection = "tutoring_offers")
public class TutoringOffer {

    @Id
    private String id;

    @NotBlank(message = "tutorId darf nicht leer sein")
    private String tutorId;

    @NotBlank(message = "tutorName darf nicht leer sein")
    private String tutorName;

    private String tutorAvatar;

    @NotBlank(message = "subjectId darf nicht leer sein")
    private String subjectId;

    @NotBlank(message = "subjectName darf nicht leer sein")
    private String subjectName;

    @NotBlank(message = "mode darf nicht leer sein")
    private String mode; // online, onsite, both

    @Size(max = 500, message = "Beschreibung darf maximal 500 Zeichen lang sein")
    private String description;

    private String availability;
}
