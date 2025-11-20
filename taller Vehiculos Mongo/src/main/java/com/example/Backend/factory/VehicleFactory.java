package com.example.Backend.factory;

import org.springframework.stereotype.Component;

import com.example.Backend.dto.VehicleDTO;
import com.example.Backend.model.Vehicle;

/**
 * Factory para crear instancias de Vehicle a partir de VehicleDTO u otros orígenes.
 * Centraliza reglas de creación y valores por defecto.
 */
@Component
public class VehicleFactory {

    /**
     * Crea una entidad Vehicle a partir de un DTO. No asigna ID (MongoDB puede generarlo).
     */
    public Vehicle createFromDto(VehicleDTO dto) {
        if (dto == null) return null;
        Vehicle v = new Vehicle();
        v.setMarca(dto.getMarca());
        v.setModelo(dto.getModelo());
        v.setAnio(dto.getAnio());
        v.setColor(dto.getColor());
        return v;
    }
}
