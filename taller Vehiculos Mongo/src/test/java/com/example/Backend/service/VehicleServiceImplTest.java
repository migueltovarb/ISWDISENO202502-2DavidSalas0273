package com.example.Backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.Backend.dto.VehicleDTO;
import com.example.Backend.factory.VehicleFactory;
import com.example.Backend.mapper.VehicleMapper;
import com.example.Backend.model.Vehicle;
import com.example.Backend.repository.VehicleRepository;
import com.example.Backend.service.impl.VehicleServiceImpl;

class VehicleServiceImplTest {

    @Mock
    private VehicleRepository repository;

    @Mock
    private VehicleMapper mapper;

    @Mock
    private VehicleFactory factory;

    @InjectMocks
    private VehicleServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_shouldSaveAndReturnDto() {
        VehicleDTO dto = new VehicleDTO(null, "Ford", "Fiesta", 2018, "Rojo");
        Vehicle toSave = new Vehicle(null, "Ford", "Fiesta", 2018, "Rojo");
        Vehicle saved = new Vehicle("1", "Ford", "Fiesta", 2018, "Rojo");

        when(factory.createFromDto(dto)).thenReturn(toSave);
        when(repository.save(any(Vehicle.class))).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(new VehicleDTO("1", "Ford", "Fiesta", 2018, "Rojo"));

        VehicleDTO result = service.create(dto);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Ford", result.getMarca());
    }

    @Test
    void getById_whenNotFound_shouldThrow() {
        when(repository.findById("x")).thenReturn(Optional.empty());
        try {
            service.getById("x");
            fail("Expected exception");
        } catch (RuntimeException ex) {
            assertTrue(ex.getMessage().contains("not found"));
        }
    }
}
