package com.vetcarepro.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VaccinationCertificateResponse {
    String id;
    String certificateNumber;
    String appointmentId;
    String petId;
    String vaccineId;
    LocalDate issueDate;
    LocalDate expirationDate;
}
