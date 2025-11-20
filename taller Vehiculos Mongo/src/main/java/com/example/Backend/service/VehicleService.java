package com.example.Backend.service;

import java.util.List;

import com.example.Backend.dto.VehicleDTO;

/**
 * Interfaz del servicio para operaciones de Vehicle.
 * Define el contrato usado por el controlador.
 */
public interface VehicleService {

    VehicleDTO create(VehicleDTO dto);

    List<VehicleDTO> getAll();

    VehicleDTO getById(String id);

    VehicleDTO update(String id, VehicleDTO dto);

    void delete(String id);
}
