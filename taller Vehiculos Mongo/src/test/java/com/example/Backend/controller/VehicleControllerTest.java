package com.example.Backend.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.example.Backend.dto.VehicleDTO;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.MediaType;

@WebMvcTest(controllers = VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private com.example.Backend.service.VehicleService vehicleService;

    @Test
    void getAll_shouldReturnOk() throws Exception {
        when(vehicleService.getAll()).thenReturn(List.of(new VehicleDTO("1","Toyota","Yaris",2019,"Azul")));
        mockMvc.perform(get("/api/vehicles")).andExpect(status().isOk());
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        VehicleDTO dto = new VehicleDTO(null, "Honda", "Civic", 2021, "Negro");
        VehicleDTO created = new VehicleDTO("99", "Honda", "Civic", 2021, "Negro");
        when(vehicleService.create(org.mockito.ArgumentMatchers.any())).thenReturn(created);

        String json = "{\"marca\":\"Honda\",\"modelo\":\"Civic\",\"anio\":2021,\"color\":\"Negro\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/vehicles").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());
    }
}
