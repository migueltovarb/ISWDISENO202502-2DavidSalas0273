package com.vetcarepro.domain.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vaccination_certificates")
public class VaccinationCertificate {

    @Id
    private String id;

    private String appointmentId;

    private String ownerId;

    private String petId;

    private String veterinarianId;

    private String vaccineId;

    private String certificateNumber;

    private LocalDate issueDate;

    private LocalDate expirationDate;

    private String storagePath;

    private byte[] pdfContent;
}
