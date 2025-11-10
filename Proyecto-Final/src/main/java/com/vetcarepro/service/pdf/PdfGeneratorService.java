package com.vetcarepro.service.pdf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vetcarepro.domain.entity.Pet;
import com.vetcarepro.domain.entity.PetOwner;
import com.vetcarepro.domain.entity.VaccinationCertificate;
import com.vetcarepro.domain.entity.Vaccine;
import com.vetcarepro.domain.entity.Veterinarian;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PdfGeneratorService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private final Path storageDirectory;

    public PdfGeneratorService(@Value("${certificate.storage-path:certificates}") String storagePath) {
        this.storageDirectory = Paths.get(storagePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.storageDirectory);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create certificate directory", e);
        }
    }

    public Path generateCertificate(VaccinationCertificate certificate,
                                    Pet pet,
                                    PetOwner owner,
                                    Vaccine vaccine,
                                    Veterinarian veterinarian) {

        Path target = storageDirectory.resolve("certificate-" + certificate.getCertificateNumber() + ".pdf");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setFont(PDType1Font.HELVETICA_BOLD, 20);
                content.newLineAtOffset(80, 750);
                content.showText("Veterinary Vaccination Certificate");
                content.endText();

                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 12);
                content.setLeading(16);
                content.newLineAtOffset(80, 700);
                content.showText("Certificate #: " + certificate.getCertificateNumber());
                content.newLine();
                content.showText("Pet: " + pet.getName() + " (" + pet.getSpecies() + ")");
                content.newLine();
                content.showText("Owner: " + owner.getFullName());
                content.newLine();
                content.showText("Vaccine: " + vaccine.getName());
                content.newLine();
                content.showText("Issued: " + DATE_FORMAT.format(certificate.getIssueDate()));
                content.newLine();
                content.showText("Expires: " + DATE_FORMAT.format(certificate.getExpirationDate()));
                content.newLine();
                content.showText("Veterinarian: " + veterinarian.getFullName());
                content.endText();
            }
            document.save(target.toFile());
            log.info("Certificate saved at {}", target);
            return target;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to generate certificate PDF", e);
        }
    }
}
