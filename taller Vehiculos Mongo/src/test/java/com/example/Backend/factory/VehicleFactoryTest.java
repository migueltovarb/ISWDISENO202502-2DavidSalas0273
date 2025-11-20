package com.example.Backend.factory;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.example.Backend.dto.VehicleDTO;
import com.example.Backend.model.Vehicle;

class VehicleFactoryTest {

    private final VehicleFactory factory = new VehicleFactory();

    @Test
    void createFromDto_shouldMapFields() {
        VehicleDTO dto = new VehicleDTO(null, "Mazda", "3", 2020, "Rojo");
        Vehicle v = factory.createFromDto(dto);
        assertNotNull(v);
        assertEquals("Mazda", v.getMarca());
        assertNull(v.getId());
    }
}
