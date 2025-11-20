package com.example.Backend.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.example.Backend.dto.VehicleDTO;
import com.example.Backend.model.Vehicle;

class VehicleMapperTest {

    private final VehicleMapper mapper = new VehicleMapper();

    @Test
    void toDto_and_toEntity_roundtrip() {
        Vehicle v = new Vehicle("1", "Kia", "Rio", 2017, "Gris");
        VehicleDTO dto = mapper.toDto(v);
        assertNotNull(dto);
        assertEquals("Kia", dto.getMarca());

        Vehicle back = mapper.toEntity(dto);
        assertNotNull(back);
        assertEquals(dto.getModelo(), back.getModelo());
    }
}
