package com.example.Backend.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.Backend.dto.VehicleDTO;
import com.example.Backend.factory.VehicleFactory;
import com.example.Backend.exception.NotFoundException;
import com.example.Backend.mapper.VehicleMapper;
import com.example.Backend.model.Vehicle;
import com.example.Backend.repository.VehicleRepository;
import com.example.Backend.service.VehicleService;

/**
 * Implementación del servicio que contiene la lógica de negocio para Vehicle.
 * El controlador delega todas las operaciones aquí.
 */
@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository repository;
    private final VehicleMapper mapper;
    private final VehicleFactory factory;

    public VehicleServiceImpl(VehicleRepository repository, VehicleMapper mapper, VehicleFactory factory) {
        this.repository = repository;
        this.mapper = mapper;
        this.factory = factory;
    }

    @Override
    public VehicleDTO create(VehicleDTO dto) {
        Vehicle entity = factory.createFromDto(dto);
        Vehicle saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public List<VehicleDTO> getAll() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public VehicleDTO getById(String id) {
        Optional<Vehicle> opt = repository.findById(id);
        Vehicle v = opt.orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + id));
        return mapper.toDto(v);
    }

    @Override
    public VehicleDTO update(String id, VehicleDTO dto) {
        Optional<Vehicle> opt = repository.findById(id);
        Vehicle existing = opt.orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + id));
        // actualizar campos (permitir solo campos de DTO)
        existing.setMarca(dto.getMarca());
        existing.setModelo(dto.getModelo());
        existing.setAnio(dto.getAnio());
        existing.setColor(dto.getColor());
        Vehicle saved = repository.save(existing);
        return mapper.toDto(saved);
    }

    @Override
    public void delete(String id) {
        Optional<Vehicle> opt = repository.findById(id);
        Vehicle existing = opt.orElseThrow(() -> new NotFoundException("Vehicle not found with id: " + id));
        repository.delete(existing);
    }
}
