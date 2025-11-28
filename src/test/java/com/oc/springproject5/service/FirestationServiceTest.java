package com.oc.springproject5.service;

import com.oc.springproject5.exception.AlreadyExistException;
import com.oc.springproject5.exception.NotFoundException;
import com.oc.springproject5.model.Firestation;
import com.oc.springproject5.model.MedicalRecord;
import com.oc.springproject5.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class FirestationServiceTest {

    @Autowired
    private FirestationService firestationService;

    @MockBean
    private DataService dataService;

    private List<Firestation> firestations;
    private List<Person> persons;

    @BeforeEach
    void setUp() {
        firestations = new ArrayList<>();
        firestations.add(new Firestation("123 rue du Barde", "1"));
        firestations.add(new Firestation("456 rue du Chêne", "2"));

        persons = new ArrayList<>();
        persons.add(new Person("John", "Doe", "123 rue du Barde", "Paris", "75000", "1111", "john@mail.com"));
        persons.add(new Person("Jeanne", "Doe", "456 rue du Chêne", "Paris", "75000", "2222", "jeanne@mail.com"));

    }


        @Test
        void testGetAllFirestations() {
            when(dataService.getAllFirestations()).thenReturn(firestations);

            List<Firestation> result = firestationService.getAllFirestations();

            assertEquals(2, result.size());
            verify(dataService, times(1)).getAllFirestations();
        }

    @Test
    void testAddFirestation_success() {
        Firestation newFirestation = new Firestation("789 rue des Fleurs", "3");
        when(dataService.getAllFirestations()).thenReturn(new ArrayList<>(firestations));

        Firestation result = firestationService.addFirestation(newFirestation);

        assertEquals("789 rue des Fleurs", result.getAddress());
        verify(dataService, times(1)).saveFirestations(anyList());
    }

    @Test
    void testAddFirestation_duplicate() {
        Firestation duplicate = new Firestation("123 rue du Barde", "1");
        when(dataService.getAllFirestations()).thenReturn(firestations);

        assertThrows(AlreadyExistException.class, () -> firestationService.addFirestation(duplicate));
        verify(dataService, never()).saveFirestations(anyList());
    }

    @Test
    void testUpdateFirestation_success() {
        when(dataService.getAllFirestations()).thenReturn(firestations);

        Firestation update = new Firestation("123 rue du Barde", "5");
        Firestation result = firestationService.updateFirestation("123 rue du Barde", update);

        assertEquals("5", result.getStation());
        verify(dataService, times(1)).saveFirestations(anyList());
    }

    @Test
    void testUpdateFirestation_notFound() {
        when(dataService.getAllFirestations()).thenReturn(firestations);

        Firestation update = new Firestation("999 rue du Lac", "10");
        assertThrows(NotFoundException.class,
                () -> firestationService.updateFirestation("999 rue du Lac", update));
        verify(dataService, never()).saveFirestations(anyList());
    }

    @Test
    void testDeleteFirestationByAddress() {
        when(dataService.getAllFirestations()).thenReturn(firestations);

        firestationService.deleteFirestationByAddress("456 rue du Chêne");

        verify(dataService, times(1)).saveFirestations(anyList());
    }

    @Test
    void testDeleteFirestationByStation() {
        when(dataService.getAllFirestations()).thenReturn(firestations);

        firestationService.deleteFirestationByStation("1");

        verify(dataService, times(1)).saveFirestations(anyList());
    }

    @Test
    void testGetPersonsCoveredByStation_success() {
        when(dataService.getAddressesByStationNumber("1"))
                .thenReturn(List.of("123 rue du Barde"));
        when(dataService.getPersonsByAddresses(anyList())).thenReturn(persons);
        when(dataService.getMedicalRecordByName("John", "Doe"))
                .thenReturn(new MedicalRecord("John", "Doe", "01/01/1990", null, null));
        when(dataService.getMedicalRecordByName("Jeanne", "Doe"))
                .thenReturn(new MedicalRecord("Jeanne", "Doe", "01/01/2015", null, null));

        Map<String, Object> result = firestationService.getPersonsCoveredByStation("1");

        assertTrue(result.containsKey("persons"));
        assertEquals(1, result.get("childCount"));
        assertEquals(1, result.get("adultCount"));
    }

    @Test
    void testGetPersonsCoveredByStation_noAddresses() {
        when(dataService.getAddressesByStationNumber("99")).thenReturn(Collections.emptyList());

        Map<String, Object> result = firestationService.getPersonsCoveredByStation("99");

        assertEquals(0, result.get("adultCount"));
        assertEquals(0, result.get("childCount"));
    }

    @Test
    void testGetPhoneNumbersByFirestation() {
        when(dataService.getAddressesByStationNumber("1"))
                .thenReturn(List.of("123 rue du Barde"));
        when(dataService.getPersonsByAddresses(anyList())).thenReturn(persons);

        List<String> result = firestationService.getPhoneNumbersByFirestation("1");

        assertEquals(2, result.size());
        assertTrue(result.contains("1111"));
        assertTrue(result.contains("2222"));
    }

    @Test
    void testGetHousesByStations_success() {
        when(dataService.getAddressesByStationNumber("1"))
                .thenReturn(List.of("123 rue du Barde"));
        when(dataService.getPersonsByAddress("123 rue du Barde"))
                .thenReturn(List.of(persons.get(0)));
        when(dataService.getMedicalRecordByName("John", "Doe"))
                .thenReturn(new MedicalRecord("John", "Doe", "01/01/1990",
                        List.of("med"), List.of("all")));

        Map<String, Object> result = firestationService.getHousesByStations("1");

        assertEquals(1, result.size());
        assertTrue(result.containsKey("123 rue du Barde"));
        List<Map<String, Object>> household = (List<Map<String, Object>>) result.get("123 rue du Barde");
        assertEquals("John", household.get(0).get("firstName"));
    }

    @Test
    void testGetHousesByStations_noAddresses() {
        when(dataService.getAddressesByStationNumber("99")).thenReturn(Collections.emptyList());

        Map<String, Object> result = firestationService.getHousesByStations("99");

        assertTrue(result.isEmpty());
    }

}
