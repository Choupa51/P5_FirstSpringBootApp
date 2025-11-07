package com.oc.springproject5.controller;

import com.oc.springproject5.model.Person;
import com.oc.springproject5.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping
    public ResponseEntity<List<Person>> getAllPersons() {
        System.out.println("Requête GET /person - Récupération de toutes les personnes");

        try {
            List<Person> persons = personService.getAllPersons();
            System.out.println("Réponse GET /person - " + persons.size() + " personnes retournées");
            return ResponseEntity.ok(persons);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des personnes : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Person> addPerson(@RequestBody Person person) {
        System.out.println("Requête POST /person - Ajout d'une personne : " +
                person.getFirstName() + " " + person.getLastName());

        try {
            Person addedPerson = personService.addPerson(person);
            System.out.println("Réponse POST /person - Personne ajoutée avec succès");
            return ResponseEntity.status(HttpStatus.CREATED).body(addedPerson);
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur lors de l'ajout de la personne : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout de la personne : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    public ResponseEntity<Person> updatePerson(@RequestParam String firstName,
                                               @RequestParam String lastName,
                                               @RequestBody Person person) {
        System.out.println("Requête PUT /person - Mise à jour de la personne : " + firstName + " " + lastName);

        try {
            Person updatedPerson = personService.updatePerson(firstName, lastName, person);
            System.out.println("Réponse PUT /person - Personne mise à jour avec succès");
            return ResponseEntity.ok(updatedPerson);
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur lors de la mise à jour de la personne : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour de la personne : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePerson(@RequestParam String firstName,
                                             @RequestParam String lastName) {
        System.out.println("Requête DELETE /person - Suppression de la personne : " + firstName + " " + lastName);

        try {
            personService.deletePerson(firstName, lastName);
            System.out.println("Réponse DELETE /person - Personne supprimée avec succès");
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur lors de la suppression de la personne : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression de la personne : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}