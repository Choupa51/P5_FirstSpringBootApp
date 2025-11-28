package com.oc.springproject5.controller;

import com.oc.springproject5.model.Person;
import com.oc.springproject5.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping
    public List<Person> getAllPersons() {
        System.out.println("Requête GET /person - Récupération de toutes les personnes");

        List<Person> persons = personService.getAllPersons();
        System.out.println("Réponse GET /person - " + persons.size() + " personnes retournées");

        return persons;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Person addPerson(@RequestBody Person person) {
        System.out.println("Requête POST /person - Ajout d'une personne : " +
                person.getFirstName() + " " + person.getLastName());

        Person addedPerson = personService.addPerson(person);
        System.out.println("Réponse POST /person - Personne ajoutée avec succès");

        return addedPerson;
    }

    @PutMapping
    public Person updatePerson(@RequestParam String firstName,
                               @RequestParam String lastName,
                               @RequestBody Person person) {
        System.out.println("Requête PUT /person - Mise à jour de la personne : " + firstName + " " + lastName);

        Person updatedPerson = personService.updatePerson(firstName, lastName, person);
        System.out.println("Réponse PUT /person - Personne mise à jour avec succès");

        return updatedPerson;
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePerson(@RequestParam String firstName,
                             @RequestParam String lastName) {
        System.out.println("Requête DELETE /person - Suppression de la personne : " + firstName + " " + lastName);

        personService.deletePerson(firstName, lastName);
        System.out.println("Réponse DELETE /person - Personne supprimée avec succès");
    }
}