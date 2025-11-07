package com.oc.springproject5.service;

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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PersonServiceTest {

    @Autowired
    private PersonService personService;

    @MockBean
    private DataService dataService;

    private List<Person> persons;

    @BeforeEach
    void setUp() {
        persons = new ArrayList<>();
        persons.add(new Person("John", "Doe", "123 rue du Barde", "Paris", "75000", "0000", "john@mail.com"));
        persons.add(new Person("Jeanne", "Doe", "123 rue du Barde", "Paris", "75000", "1111", "jeanne@mail.com"));

        Person child = new Person("Alice", "Doe", "321 Bd Detour", "Paris", "75000", "010203", "alice@mail.com");
        Person parent = new Person("Bob", "Doe", "321 Bd Detour", "Paris", "75000", "040506", "bob@mail.com");
    }

    @Test
    public void testGetAllPersons() {
        when(dataService.getAllPersons()).thenReturn(persons);

        List<Person> result = personService.getAllPersons();

        assertEquals(2, result.size());
        verify(dataService, times(1)).getAllPersons();

    }

    @Test
    public void testAddPerson() {
        when(dataService.getAllPersons()).thenReturn(new ArrayList<>(persons));

        Person newPerson = new Person("Paul", "Smith", "456 Rue", "Lyon", "69000", "2222", "paul@mail.com");
        Person result = personService.addPerson(newPerson);

        assertEquals("Paul", result.getFirstName());
        verify(dataService, times(1)).savePersons(anyList());
    };

    @Test
    public void testUpdatePerson() {
        when(dataService.getAllPersons()).thenReturn(persons);

        Person update = new Person();
        update.setCity("Lyon");
        update.setPhone("9999");

        Person result = personService.updatePerson("John", "Doe", update);

        assertEquals("Lyon", result.getCity());
        assertEquals("9999", result.getPhone());
        verify(dataService, times(1)).savePersons(anyList());
    };

    @Test
    void testDeletePerson() {
        when(dataService.getAllPersons()).thenReturn(persons);

        personService.deletePerson("John", "Doe");

        verify(dataService, times(1)).savePersons(anyList());
    }

    @Test
    void testGetChildrenAddress() {
        when(dataService.getPersonsByAddress("123 rue du Barde")).thenReturn(persons);

        MedicalRecord adultRecord = new MedicalRecord("John", "Doe", "01/01/1990", null, null);
        MedicalRecord childRecord = new MedicalRecord("Jeanne", "Doe", "01/01/2015", null, null);

        when(dataService.getMedicalRecordByName("John", "Doe")).thenReturn(adultRecord);
        when(dataService.getMedicalRecordByName("Jeanne", "Doe")).thenReturn(childRecord);

        // Appel du service
        List<Map<String, Object>> result = personService.getChildrenAddress("123 rue du Barde");

        // Vérifications
        assertEquals(1, result.size());
        assertEquals("Jeanne", result.get(0).get("firstName"));
        assertTrue(result.get(0).containsKey("householdMembers"));
        assertEquals(1, ((List<?>) result.get(0).get("householdMembers")).size());
    }

    @Test
    void testGetResidentsAndFirestationAddress_validAddress() {
        when(dataService.getPersonsByAddress("123 rue du Barde")).thenReturn(persons);
        when(dataService.getFirestationNumberAddress("123 rue du Barde")).thenReturn("2");

        MedicalRecord johnRecord = new MedicalRecord("John", "Doe", "01/01/1990",
                List.of("med1"), List.of("all1"));
        MedicalRecord jeanneRecord = new MedicalRecord("Jeanne", "Doe", "01/01/2015",
                List.of("med2"), List.of("all2"));

        when(dataService.getMedicalRecordByName("John", "Doe")).thenReturn(johnRecord);
        when(dataService.getMedicalRecordByName("Jeanne", "Doe")).thenReturn(jeanneRecord);

        Map<String, Object> result = personService.getResidentsAndFirestationAddress("123 rue du Barde");

        assertEquals("2", result.get("stationNumber"));
        List<Map<String, Object>> residents = (List<Map<String, Object>>) result.get("residents");
        assertEquals(2, residents.size());
    }

    @Test
    void testGetPersonInfoByLastName_found() {
        when(dataService.getPersonsByLastName("Doe")).thenReturn(persons);
        when(dataService.getMedicalRecordByName(anyString(), anyString()))
                .thenReturn(new MedicalRecord("John", "Doe", "01/01/1990",
                        List.of("med"), List.of("all")));

        List<Map<String, Object>> result = personService.getPersonInfoByLastName("Doe");

        assertEquals(2, result.size());
        assertEquals("Doe", result.get(0).get("lastName"));
        assertTrue(result.get(0).containsKey("age"));
        assertTrue(result.get(0).containsKey("medications"));
        assertTrue(result.get(0).containsKey("allergies"));
    }

    @Test
    void testGetEmailsByCity_withTwoEmails() {
        when(dataService.getPersonsByCity("Paris")).thenReturn(persons);

        List<String> result = personService.getEmailsByCity("Paris");

        assertEquals(2, result.size());
        assertTrue(result.contains("john@mail.com"));
        assertTrue(result.contains("jeanne@mail.com"));
    }

    @Test
    void testGetEmailsByCity_emptyCity() {
        when(dataService.getPersonsByCity("Lyon")).thenReturn(Collections.emptyList());

        List<String> result = personService.getEmailsByCity("Lyon");

        assertTrue(result.isEmpty());
    }


}
