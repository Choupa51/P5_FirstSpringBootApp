package com.oc.springproject5.controller;

import com.oc.springproject5.service.FirestationService;
import com.oc.springproject5.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AlertsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    @MockBean
    private FirestationService firestationService;

    private List<Map<String, Object>> childrenList;
    private Map<String, Object> fireResponse;
    private Map<String, Object> floodResponse;

    @BeforeEach
    void setUp() {
        // Mock pour /childAlert
        Map<String, Object> child = new HashMap<>();
        child.put("firstName", "Emma");
        child.put("lastName", "Doe");
        child.put("age", 8);
        childrenList = List.of(child);

        // Mock pour /fire
        Map<String, Object> resident = new HashMap<>();
        resident.put("firstName", "John");
        resident.put("lastName", "Doe");
        resident.put("phone", "123-456");
        resident.put("age", 40);

        fireResponse = new HashMap<>();
        fireResponse.put("stationNumber", "2");
        fireResponse.put("residents", List.of(resident));

        // Mock pour /flood/stations
        Map<String, Object> house = new HashMap<>();
        house.put("address", "123 rue du Barde");
        house.put("residents", List.of(resident));

        floodResponse = new HashMap<>();
        floodResponse.put("2", List.of(house));
    }

    // ---------- /childAlert ----------
    @Test
    void testGetChildrenAddress_Success() throws Exception {
        when(personService.getChildrenAddress("123 rue du Barde")).thenReturn(childrenList);

        mockMvc.perform(get("/childAlert")
                        .param("address", "123 rue du Barde"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Emma"))
                .andExpect(jsonPath("$[0].age").value(8));

        verify(personService, times(1)).getChildrenAddress("123 rue du Barde");
    }

    @Test
    void testGetChildrenAddress_NoChildren() throws Exception {
        when(personService.getChildrenAddress("999 rue Inconnue")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/childAlert")
                        .param("address", "999 rue Inconnue"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));

        verify(personService, times(1)).getChildrenAddress("999 rue Inconnue");
    }

    @Test
    void testGetChildrenAddress_InternalError() throws Exception {
        when(personService.getChildrenAddress(anyString())).thenThrow(new RuntimeException("Erreur interne"));

        mockMvc.perform(get("/childAlert")
                        .param("address", "Erreur"))
                .andExpect(status().isInternalServerError());
    }

    // ---------- /phoneAlert ----------
    @Test
    void testGetPhoneNumbersByFirestation_Success() throws Exception {
        List<String> phones = Arrays.asList("111-222", "333-444");
        when(firestationService.getPhoneNumbersByFirestation("1")).thenReturn(phones);

        mockMvc.perform(get("/phoneAlert")
                        .param("firestation", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("111-222"))
                .andExpect(jsonPath("$[1]").value("333-444"));

        verify(firestationService, times(1)).getPhoneNumbersByFirestation("1");
    }

    @Test
    void testGetPhoneNumbersByFirestation_InternalError() throws Exception {
        when(firestationService.getPhoneNumbersByFirestation(anyString()))
                .thenThrow(new RuntimeException("Erreur interne"));

        mockMvc.perform(get("/phoneAlert")
                        .param("firestation", "99"))
                .andExpect(status().isInternalServerError());
    }

    // ---------- /fire ----------
    @Test
    void testGetResidentsByFirestationAddress_Success() throws Exception {
        when(personService.getResidentsAndFirestationAddress("123 rue du Barde"))
                .thenReturn(fireResponse);

        mockMvc.perform(get("/fire")
                        .param("address", "123 rue du Barde"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stationNumber").value("2"))
                .andExpect(jsonPath("$.residents[0].firstName").value("John"))
                .andExpect(jsonPath("$.residents[0].age").value(40));

        verify(personService, times(1))
                .getResidentsAndFirestationAddress("123 rue du Barde");
    }

    @Test
    void testGetResidentsByFirestationAddress_InternalError() throws Exception {
        when(personService.getResidentsAndFirestationAddress(anyString()))
                .thenThrow(new RuntimeException("Erreur interne"));

        mockMvc.perform(get("/fire")
                        .param("address", "Erreur"))
                .andExpect(status().isInternalServerError());
    }

    // ---------- /flood/stations ----------
    @Test
    void testGetHousesByStations_Success() throws Exception {
        when(firestationService.getHousesByStations("2")).thenReturn(floodResponse);

        mockMvc.perform(get("/flood/stations")
                        .param("stations", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.2[0].address").value("123 rue du Barde"))
                .andExpect(jsonPath("$.2[0].residents[0].firstName").value("John"));

        verify(firestationService, times(1)).getHousesByStations("2");
    }

    @Test
    void testGetHousesByStations_InternalError() throws Exception {
        when(firestationService.getHousesByStations(anyString()))
                .thenThrow(new RuntimeException("Erreur interne"));

        mockMvc.perform(get("/flood/stations")
                        .param("stations", "99"))
                .andExpect(status().isInternalServerError());
    }
}
