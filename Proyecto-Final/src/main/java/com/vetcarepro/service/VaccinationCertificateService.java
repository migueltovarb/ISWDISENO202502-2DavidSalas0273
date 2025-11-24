package com.vetcarepro.service;

import java.nio.file.Path;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vetcarepro.domain.builder.VaccinationCertificateBuilder;
import com.vetcarepro.domain.entity.Appointment;
import com.vetcarepro.domain.entity.Pet;
import com.vetcarepro.domain.entity.PetOwner;
import com.vetcarepro.domain.entity.VaccinationCertificate;
import com.vetcarepro.domain.entity.Vaccine;
import com.vetcarepro.domain.entity.Veterinarian;
import com.vetcarepro.domain.enums.AppointmentType;
import com.vetcarepro.dto.VaccinationCertificateResponse;
import com.vetcarepro.exception.BusinessRuleException;
import com.vetcarepro.exception.ResourceNotFoundException;
import com.vetcarepro.repository.VaccinationCertificateRepository;
import com.vetcarepro.service.pdf.PdfGeneratorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VaccinationCertificateService {

    private final VaccinationCertificateRepository certificateRepository;
    private final PetService petService;
    private final PetOwnerService ownerService;
    private final VeterinarianService veterinarianService;
    private final VaccineService vaccineService;
    private final PdfGeneratorService pdfGeneratorService;

    @Transactional
    public VaccinationCertificate generateFromAppointment(Appointment appointment) {
        if (appointment.getType() != AppointmentType.VACCINATION) {
            throw new BusinessRuleException("Certificates can only be generated for vaccination appointments");
        }
        certificateRepository.findByAppointmentId(appointment.getId())
            .ifPresent(existing -> {
                throw new BusinessRuleException("Certificate already exists for appointment " + appointment.getId());
            });

        Pet pet = petService.findById(appointment.getPetId());
        PetOwner owner = ownerService.findById(appointment.getOwnerId());
        Veterinarian vet = veterinarianService.findById(appointment.getVeterinarianId());
        Vaccine vaccine = vaccineService.findById(appointment.getVaccineId());

        LocalDate issueDate = appointment.getAppointmentDate().toLocalDate();
        LocalDate expiration = issueDate.plusDays(vaccine.getValidityDays());

        VaccinationCertificate certificate = VaccinationCertificateBuilder.builder()
            .fromAppointment(appointment)
            .withOwner(owner)
            .withPet(pet)
            .withVeterinarian(vet)
            .withVaccine(vaccine)
            .validity(issueDate, expiration)
            .build();

        Path pdfPath = pdfGeneratorService.generateCertificate(certificate, pet, owner, vaccine, vet);
        certificate.setStoragePath(pdfPath.toString());
        try {
            certificate.setPdfContent(java.nio.file.Files.readAllBytes(pdfPath));
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Unable to read certificate content", e);
        }

        return certificateRepository.save(certificate);
    }

    public VaccinationCertificate download(String id) {
        return certificateRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Certificate not found: " + id));
    }

    public java.util.List<VaccinationCertificate> findByPet(String petId) {
        return certificateRepository.findByPetId(petId);
    }

    public VaccinationCertificateResponse toResponse(VaccinationCertificate certificate) {
        return VaccinationCertificateResponse.builder()
            .id(certificate.getId())
            .certificateNumber(certificate.getCertificateNumber())
            .appointmentId(certificate.getAppointmentId())
            .petId(certificate.getPetId())
            .vaccineId(certificate.getVaccineId())
            .issueDate(certificate.getIssueDate())
            .expirationDate(certificate.getExpirationDate())
            .build();
    }
}
