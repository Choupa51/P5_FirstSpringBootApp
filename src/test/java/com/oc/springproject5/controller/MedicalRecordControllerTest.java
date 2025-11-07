package com.oc.springproject5.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oc.springproject5.model.MedicalRecord;
import com.oc.springproject5.service.MedicalRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicalRecordService medicalRecordService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<MedicalRecord> medicalRecords;

    @BeforeEach
    void setUp() {
        medicalRecords = Arrays.asList(
                new MedicalRecord("John", "Doe", "01/01/1980",
                        Arrays.asList("aspirin:100mg"), Arrays.asList("peanut")),
                new MedicalRecord("Jane", "Doe", "02/02/1990",
                        Arrays.asList("ibuprofen:200mg"), Arrays.asList("pollen"))
        );
    }

    // GET
    @Test
    void testGetAllMedicalRecords() throws Exception {
        when(medicalRecordService.getAllMedicalRecords()).thenReturn(medicalRecords);

        mockMvc.perform(get("/medicalRecord"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].lastName").value("Doe"));

        verify(medicalRecordService, times(1)).getAllMedicalRecords();
    }

    // POST
    @Test
    void testAddMedicalRecord_Success() throws Exception {
        MedicalRecord newRecord = new MedicalRecord("Alice", "Smith", "03/03/2000",
                Arrays.asList("vitamin:500mg"), Arrays.asList("gluten"));

        when(medicalRecordService.addMedicalRecord(any(MedicalRecord.class))).thenReturn(newRecord);

        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRecord)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Smith"));

        verify(medicalRecordService, times(1)).addMedicalRecord(any(MedicalRecord.class));
    }

    @Test
    void testAddMedicalRecord_Conflict() throws Exception {
        MedicalRecord duplicate = new MedicalRecord("John", "Doe", "01/01/1980",
                Arrays.asList("aspirin:100mg"), Arrays.asList("peanut"));

        when(medicalRecordService.addMedicalRecord(any(MedicalRecord.class)))
                .thenThrow(new IllegalArgumentException("Ce dossier médical existe déjà"));

        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict());

        verify(medicalRecordService, times(1)).addMedicalRecord(any(MedicalRecord.class));
    }

    // PUT
    @Test
    void testUpdateMedicalRecord_Success() throws Exception {
        MedicalRecord updated = new MedicalRecord("John", "Doe", "01/01/1985",
                Arrays.asList("paracetamol:500mg"), Arrays.asList("none"));

        when(medicalRecordService.updateMedicalRecord(eq("John"), eq("Doe"), any(MedicalRecord.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/medicalRecord")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.birthdate").value("01/01/1985"))
                .andExpect(jsonPath("$.medications[0]").value("paracetamol:500mg"));

        verify(medicalRecordService, times(1))
                .updateMedicalRecord(eq("John"), eq("Doe"), any(MedicalRecord.class));
    }

    @Test
    void testUpdateMedicalRecord_NotFound() throws Exception {
        MedicalRecord updated = new MedicalRecord("Bob", "Smith", "05/05/1995",
                Arrays.asList("none"), Arrays.asList("none"));

        when(medicalRecordService.updateMedicalRecord(eq("Bob"), eq("Smith"), any(MedicalRecord.class)))
                .thenThrow(new IllegalArgumentException("Dossier médical non trouvé"));

        mockMvc.perform(put("/medicalRecord")
                        .param("firstName", "Bob")
                        .param("lastName", "Smith")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());

        verify(medicalRecordService, times(1))
                .updateMedicalRecord(eq("Bob"), eq("Smith"), any(MedicalRecord.class));
    }

    // DELETE
    @Test
    void testDeleteMedicalRecord_Success() throws Exception {
        doNothing().when(medicalRecordService).deleteMedicalRecord("John", "Doe");

        mockMvc.perform(delete("/medicalRecord")
                        .param("firstName", "John")
                        .param("lastName", "Doe"))
                .andExpect(status().isNoContent());

        verify(medicalRecordService, times(1))
                .deleteMedicalRecord("John", "Doe");
    }

    @Test
    void testDeleteMedicalRecord_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Dossier médical non trouvé"))
                .when(medicalRecordService).deleteMedicalRecord("Bob", "Smith");

        mockMvc.perform(delete("/medicalRecord")
                        .param("firstName", "Bob")
                        .param("lastName", "Smith"))
                .andExpect(status().isNotFound());

        verify(medicalRecordService, times(1))
                .deleteMedicalRecord("Bob", "Smith");
    }
}

