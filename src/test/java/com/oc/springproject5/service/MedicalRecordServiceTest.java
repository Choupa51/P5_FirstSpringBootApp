package com.oc.springproject5.service;

import com.oc.springproject5.exception.AlreadyExistException;
import com.oc.springproject5.exception.NotFoundException;
import com.oc.springproject5.model.MedicalRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MedicalRecordServiceTest {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @MockBean
    private DataService dataService;

    private List<MedicalRecord> medicalRecords;

    @BeforeEach

    void setUp() {
        medicalRecords = new ArrayList<>();
        medicalRecords.add(new MedicalRecord("John", "Doe", "01/01/1980",
                Arrays.asList("aspirin:100mg"), Arrays.asList("peanut")));
        medicalRecords.add(new MedicalRecord("Jeanne", "Doe", "02/02/1990",
                Arrays.asList("ibuprofen:200mg"), Arrays.asList("pollen")));
    }

    @Test
    void testGetAllMedicalRecords() {
        when(dataService.getAllMedicalRecords()).thenReturn(medicalRecords);

        List<MedicalRecord> result = medicalRecordService.getAllMedicalRecords();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
        verify(dataService, times(1)).getAllMedicalRecords();
    }

    @Test
    void testAddMedicalRecord_Success() {
        MedicalRecord newRecord = new MedicalRecord("Alice", "Smith", "03/03/2000",
                Arrays.asList("vitamin:500mg"), Arrays.asList("gluten"));

        when(dataService.getAllMedicalRecords()).thenReturn(medicalRecords);

        MedicalRecord result = medicalRecordService.addMedicalRecord(newRecord);

        assertThat(result.getFirstName()).isEqualTo("Alice");
        verify(dataService).saveMedicalRecords(anyList());
    }

    @Test
    void testAddMedicalRecord_AlreadyExists() {
        MedicalRecord duplicate = new MedicalRecord("John", "Doe", "01/01/1980",
                Arrays.asList("aspirin:100mg"), Arrays.asList("peanut"));

        when(dataService.getAllMedicalRecords()).thenReturn(medicalRecords);

        assertThrows(AlreadyExistException.class, () ->
                medicalRecordService.addMedicalRecord(duplicate));

        verify(dataService, never()).saveMedicalRecords(anyList());
    }

    @Test
    void testUpdateMedicalRecord_Success() {
        when(dataService.getAllMedicalRecords()).thenReturn(medicalRecords);

        MedicalRecord updated = new MedicalRecord("John", "Doe", "01/01/1985",
                Arrays.asList("paracetamol:500mg"), Arrays.asList("none"));

        MedicalRecord result = medicalRecordService.updateMedicalRecord("John", "Doe", updated);

        assertThat(result.getBirthdate()).isEqualTo("01/01/1985");
        assertThat(result.getMedications()).contains("paracetamol:500mg");
        verify(dataService).saveMedicalRecords(anyList());
    }

    @Test
    void testUpdateMedicalRecord_NotFound() {
        when(dataService.getAllMedicalRecords()).thenReturn(medicalRecords);

        MedicalRecord updated = new MedicalRecord("Bob", "Smith", "05/05/1995",
                Arrays.asList("none"), Arrays.asList("none"));

        assertThrows(NotFoundException.class, () ->
                medicalRecordService.updateMedicalRecord("Bob", "Smith", updated));

        verify(dataService, never()).saveMedicalRecords(anyList());
    }

    @Test
    void testDeleteMedicalRecord_Success() {
        when(dataService.getAllMedicalRecords()).thenReturn(medicalRecords);

        medicalRecordService.deleteMedicalRecord("John", "Doe");

        ArgumentCaptor<List<MedicalRecord>> captor = ArgumentCaptor.forClass(List.class);
        verify(dataService).saveMedicalRecords(captor.capture());

        List<MedicalRecord> savedList = captor.getValue();
        assertThat(savedList).hasSize(1);
        assertThat(savedList.get(0).getFirstName()).isEqualTo("Jeanne");
    }

    @Test
    void testDeleteMedicalRecord_NotFound() {
        when(dataService.getAllMedicalRecords()).thenReturn(medicalRecords);

        assertThrows(NotFoundException.class, () ->
                medicalRecordService.deleteMedicalRecord("Alice", "Smith"));

        verify(dataService, never()).saveMedicalRecords(anyList());
    }
}
