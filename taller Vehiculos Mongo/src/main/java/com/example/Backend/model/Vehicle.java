package com.example.Backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entidad Vehicle que representa un vehículo en la colección "vehicles" de MongoDB.
 *
 * Campos:
 * - id: Identificador único generado por MongoDB
 * - marca: Marca del vehículo
 * - modelo: Modelo del vehículo
 * - anio: Año de fabricación
 * - color: Color del vehículo
 */
@Document(collection = "vehicles")
public class Vehicle {

    @Id
    private String id;

    private String marca;
    private String modelo;
    private Integer anio;
    private String color;

    public Vehicle() {
    }

    public Vehicle(String id, String marca, String modelo, Integer anio, String color) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", anio=" + anio +
                ", color='" + color + '\'' +
                '}';
    }
}
