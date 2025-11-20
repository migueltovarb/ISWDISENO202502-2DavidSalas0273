package com.example.Backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.Backend.model.Vehicle;

/**
 * Repository para Vehicle usando MongoRepository.
 * Implementa operaciones CRUD básicas y posibilidad de extender con consultas personalizadas.
 */
public interface VehicleRepository extends MongoRepository<Vehicle, String> {

}
