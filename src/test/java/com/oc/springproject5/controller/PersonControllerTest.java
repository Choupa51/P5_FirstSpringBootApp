package com.oc.springproject5.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oc.springproject5.exception.AlreadyExistException;
import com.oc.springproject5.exception.NotFoundException;
import com.oc.springproject5.model.Person;
import com.oc.springproject5.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class PersonControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    @Autowired
    private ObjectMapper objectMapper;

    private Person john;
    private Person jeanne;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        john = new Person("John", "Doe", "123 rue du Barde", "Paris", "75000", "0000", "john@mail.com");
        jeanne = new Person("Jeanne", "Doe", "123 rue du Barde", "Paris", "75000", "1111", "jeanne@mail.com");
    }
    @Test
    void testGetAllPersons() throws Exception {
        List<Person> persons = Arrays.asList(john, jeanne);
        when(personService.getAllPersons()).thenReturn(persons);

        mockMvc.perform(get("/person"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jeanne"));

        verify(personService, times(1)).getAllPersons();
    }

    @Test
    void testAddPerson_Success() throws Exception {
        when(personService.addPerson(any(Person.class))).thenReturn(john);

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(john)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(personService, times(1)).addPerson(any(Person.class));
    }

    @Test
    void testAddPerson_Conflict() throws Exception {
        when(personService.addPerson(any(Person.class))).thenThrow(new AlreadyExistException("Person already exists"));

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(john)))
                .andExpect(status().isConflict());

        verify(personService, times(1)).addPerson(any(Person.class));
    }

    @Test
    void testUpdatePerson_Success() throws Exception {
        Person updated = new Person("John", "Doe", "123 rue du Barde", "Lyon", "69000", "9999", "john@mail.com");
        when(personService.updatePerson(eq("John"), eq("Doe"), any(Person.class))).thenReturn(updated);

        mockMvc.perform(put("/person")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Lyon"))
                .andExpect(jsonPath("$.phone").value("9999"));

        verify(personService, times(1)).updatePerson(eq("John"), eq("Doe"), any(Person.class));
    }

    @Test
    void testUpdatePerson_NotFound() throws Exception {
        when(personService.updatePerson(eq("Ghost"), eq("Person"), any(Person.class)))
                .thenThrow(new NotFoundException("Person not found"));

        mockMvc.perform(put("/person")
                        .param("firstName", "Ghost")
                        .param("lastName", "Person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(john)))
                .andExpect(status().isNotFound());

        verify(personService, times(1)).updatePerson(eq("Ghost"), eq("Person"), any(Person.class));
    }

    @Test
    void testDeletePerson_Success() throws Exception {
        mockMvc.perform(delete("/person")
                        .param("firstName", "John")
                        .param("lastName", "Doe"))
                .andExpect(status().isNoContent());

        verify(personService, times(1)).deletePerson("John", "Doe");
    }

    @Test
    void testDeletePerson_NotFound() throws Exception {
        doThrow(new NotFoundException("Person not found"))
                .when(personService).deletePerson("Ghost", "Unknown");

        mockMvc.perform(delete("/person")
                        .param("firstName", "Ghost")
                        .param("lastName", "Unknown"))
                .andExpect(status().isNotFound());

        verify(personService, times(1)).deletePerson("Ghost", "Unknown");
    }

}
