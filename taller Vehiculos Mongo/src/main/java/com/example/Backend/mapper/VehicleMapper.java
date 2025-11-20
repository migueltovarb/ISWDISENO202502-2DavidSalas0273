package com.example.Backend.mapper;

import org.springframework.stereotype.Component;

import com.example.Backend.dto.VehicleDTO;
import com.example.Backend.model.Vehicle;

/**
 * Mapper para convertir entre Vehicle (entidad) y VehicleDTO (DTO).
 * Separa la lógica de transformación para mantener limpio el servicio.
 */
@Component
public class VehicleMapper {

    /**
     * Convierte entidad a DTO.
     */
    public VehicleDTO toDto(Vehicle entity) {
        if (entity == null) return null;
        return new VehicleDTO(entity.getId(), entity.getMarca(), entity.getModelo(), entity.getAnio(), entity.getColor());
    }

    /**
     * Convierte DTO a entidad (sin id).
     */
    public Vehicle toEntity(VehicleDTO dto) {
        if (dto == null) return null;
        Vehicle v = new Vehicle();
        v.setId(dto.getId());
        v.setMarca(dto.getMarca());
        v.setModelo(dto.getModelo());
        v.setAnio(dto.getAnio());
        v.setColor(dto.getColor());
        return v;
    }
}
