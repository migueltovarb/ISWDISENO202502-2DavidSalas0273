package com.vetcarepro.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.vetcarepro.domain.entity.Pet;
import com.vetcarepro.domain.entity.Appointment;
import com.vetcarepro.domain.enums.Role;
import com.vetcarepro.dto.PetRequest;
import com.vetcarepro.security.SecurityUtils;
import com.vetcarepro.security.UserAccountPrincipal;
import com.vetcarepro.service.AppointmentService;
import com.vetcarepro.service.PetService;
import com.vetcarepro.service.PetOwnerService;
import com.vetcarepro.service.VeterinarianService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;
    private final PetOwnerService petOwnerService;
    private final AppointmentService appointmentService;
    private final VeterinarianService veterinarianService;

    @GetMapping
    public List<Pet> list(@RequestParam(required = false) String ownerId) {
        UserAccountPrincipal currentUser = SecurityUtils.getCurrentUser();
        if (currentUser.getRole() == Role.ADMIN) {
            if (ownerId != null) {
                return petService.findByOwner(ownerId);
            }
            return petService.findAll();
        }
        if (currentUser.getRole() == Role.OWNER) {
            return petService.findByOwner(resolveOwnerId(currentUser));
        }
        String veterinarianId = resolveVeterinarianId(currentUser);
        List<Appointment> vetAppointments = appointmentService.findByVeterinarian(veterinarianId);
        List<String> petIds = vetAppointments.stream()
            .map(Appointment::getPetId)
            .distinct()
            .toList();
        return petService.findByIds(petIds);
    }

    @PostMapping
    public Pet create(@Valid @RequestBody PetRequest request) {
        UserAccountPrincipal currentUser = SecurityUtils.getCurrentUser();
        if (currentUser.getRole() == Role.VETERINARIAN) {
            throw new AccessDeniedException("Veterinarians cannot create pets");
        }
        if (currentUser.getRole() == Role.OWNER) {
            String ownerId = resolveOwnerId(currentUser);
            if (!ownerId.equals(request.getOwnerId())) {
                throw new AccessDeniedException("Owners can only create their own pets");
            }
        }
        Pet pet = Pet.builder()
            .ownerId(request.getOwnerId())
            .name(request.getName())
            .species(request.getSpecies())
            .breed(request.getBreed())
            .birthDate(request.getBirthDate())
            .microchipId(request.getMicrochipId())
            .neutered(request.isNeutered())
            .build();
        return petService.create(pet);
    }

    @GetMapping("/{id}")
    public Pet get(@PathVariable String id) {
        Pet pet = petService.findById(id);
        verifyPetAccess(pet, SecurityUtils.getCurrentUser());
        return pet;
    }

    @PutMapping("/{id}")
    public Pet update(@PathVariable String id, @Valid @RequestBody PetRequest request) {
        Pet existing = petService.findById(id);
        UserAccountPrincipal currentUser = SecurityUtils.getCurrentUser();
        verifyPetAccess(existing, currentUser);

        String ownerId = request.getOwnerId();
        if (currentUser.getRole() == Role.OWNER) {
            ownerId = resolveOwnerId(currentUser);
        }

        Pet pet = Pet.builder()
            .ownerId(ownerId)
            .name(request.getName())
            .species(request.getSpecies())
            .breed(request.getBreed())
            .birthDate(request.getBirthDate())
            .microchipId(request.getMicrochipId())
            .neutered(request.isNeutered())
            .build();
        return petService.update(id, pet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        Pet pet = petService.findById(id);
        verifyPetAccess(pet, SecurityUtils.getCurrentUser());
        petService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void verifyPetAccess(Pet pet, UserAccountPrincipal currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }
        if (currentUser.getRole() == Role.OWNER) {
            String ownerId = resolveOwnerId(currentUser);
            if (!pet.getOwnerId().equals(ownerId)) {
                throw new AccessDeniedException("Owners can only access their own pets");
            }
            return;
        }
        String veterinarianId = resolveVeterinarianId(currentUser);
        boolean vetHasAppointment = appointmentService.findByVeterinarian(veterinarianId)
            .stream()
            .anyMatch(appointment -> appointment.getPetId().equals(pet.getId()));
        if (!vetHasAppointment) {
            throw new AccessDeniedException("Veterinarians can only access their own patients");
        }
    }

    private String resolveOwnerId(UserAccountPrincipal currentUser) {
        if (currentUser.getReferenceId() != null) {
            return currentUser.getReferenceId();
        }
        return petOwnerService.findByUserAccountId(currentUser.getId()).getId();
    }

    private String resolveVeterinarianId(UserAccountPrincipal currentUser) {
        if (currentUser.getReferenceId() != null) {
            return currentUser.getReferenceId();
        }
        return veterinarianService.findByUserAccountId(currentUser.getId()).getId();
    }
}
