package com.vetcarepro;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.vetcarepro.repository.AppointmentRepository;
import com.vetcarepro.repository.MedicalHistoryRecordRepository;
import com.vetcarepro.repository.PetOwnerRepository;
import com.vetcarepro.repository.PetRepository;
import com.vetcarepro.repository.UserAccountRepository;
import com.vetcarepro.repository.VaccinationCertificateRepository;
import com.vetcarepro.repository.VaccineRepository;
import com.vetcarepro.repository.VeterinarianRepository;

@SpringBootTest(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,"
        + "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,"
        + "de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration"
})
@ActiveProfiles("test")
class VetCareProApplicationTests {

    @MockBean
    PetOwnerRepository petOwnerRepository;
    @MockBean
    PetRepository petRepository;
    @MockBean
    VeterinarianRepository veterinarianRepository;
    @MockBean
    AppointmentRepository appointmentRepository;
    @MockBean
    VaccineRepository vaccineRepository;
    @MockBean
    VaccinationCertificateRepository vaccinationCertificateRepository;
    @MockBean
    MedicalHistoryRecordRepository medicalHistoryRecordRepository;
    @MockBean
    UserAccountRepository userAccountRepository;

    @Test
    void contextLoads() {
    }
}
