package com.oc.springproject5.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.oc.springproject5.model.Firestation;
import com.oc.springproject5.service.FirestationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FirestationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FirestationService firestationService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Firestation> firestations;

    @BeforeEach
    void setUp() {
        firestations = new ArrayList<>();
        firestations.add(new Firestation("123 rue du Barde", "1"));
        firestations.add(new Firestation("456 avenue des Sapins", "2"));
    }

    @Test
    void testGetAllFirestations() throws Exception {
        when(firestationService.getAllFirestations()).thenReturn(firestations);

        mockMvc.perform(get("/firestation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].address").value("123 rue du Barde"))
                .andExpect(jsonPath("$[1].station").value("2"));

        verify(firestationService, times(1)).getAllFirestations();
    }

    @Test
    void testGetPersonsCoveredByStation() throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("persons", List.of(Map.of("firstName", "John", "lastName", "Doe")));
        response.put("adultCount", 1);
        response.put("childCount", 0);

        when(firestationService.getPersonsCoveredByStation("1")).thenReturn(response);

        mockMvc.perform(get("/firestation").param("stationNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.persons[0].firstName").value("John"))
                .andExpect(jsonPath("$.adultCount").value(1));

        verify(firestationService, times(1)).getPersonsCoveredByStation("1");
    }

    @Test
    void testAddFirestation_success() throws Exception {
        Firestation newFirestation = new Firestation("789 chemin du Loup", "3");
        when(firestationService.addFirestation(any(Firestation.class))).thenReturn(newFirestation);

        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFirestation)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address").value("789 chemin du Loup"))
                .andExpect(jsonPath("$.station").value("3"));

        verify(firestationService, times(1)).addFirestation(any(Firestation.class));
    }

    @Test
    void testAddFirestation_conflict() throws Exception {
        Firestation conflictFirestation = new Firestation("123 rue du Barde", "1");
        when(firestationService.addFirestation(any(Firestation.class)))
                .thenThrow(new IllegalArgumentException("Mapping déjà existant"));

        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conflictFirestation)))
                .andExpect(status().isConflict());

        verify(firestationService, times(1)).addFirestation(any(Firestation.class));
    }

    @Test
    void testUpdateFirestation_success() throws Exception {
        Firestation updated = new Firestation("123 rue du Barde", "5");
        when(firestationService.updateFirestation(eq("123 rue du Barde"), any(Firestation.class))).thenReturn(updated);

        mockMvc.perform(put("/firestation")
                        .param("address", "123 rue du Barde")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.station").value("5"));

        verify(firestationService, times(1)).updateFirestation(eq("123 rue du Barde"), any(Firestation.class));
    }

    @Test
    void testUpdateFirestation_notFound() throws Exception {
        when(firestationService.updateFirestation(anyString(), any(Firestation.class)))
                .thenThrow(new IllegalArgumentException("Aucun mapping trouvé"));

        mockMvc.perform(put("/firestation")
                        .param("address", "999 rue Inconnue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Firestation("999 rue Inconnue", "9"))))
                .andExpect(status().isNotFound());

        verify(firestationService, times(1)).updateFirestation(anyString(), any(Firestation.class));
    }

    @Test
    void testDeleteFirestationByAddress_success() throws Exception {
        mockMvc.perform(delete("/firestation").param("address", "123 rue du Barde"))
                .andExpect(status().isNoContent());

        verify(firestationService, times(1)).deleteFirestationByAddress("123 rue du Barde");
    }

    @Test
    void testDeleteFirestationByStation_success() throws Exception {
        mockMvc.perform(delete("/firestation").param("station", "2"))
                .andExpect(status().isNoContent());

        verify(firestationService, times(1)).deleteFirestationByStation("2");
    }



}
