package com.oc.springproject5.controller;

import com.oc.springproject5.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/personInfo")
public class PersonInfoController {

    @Autowired
    private PersonService personService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getPersonInfoByLastName(@RequestParam String lastName) {
        System.out.println("Requête GET /personInfo - Récupération des informations pour le nom : " + lastName);

        try {
            List<Map<String, Object>> personsInfo = personService.getPersonInfoByLastName(lastName);

            if (personsInfo.isEmpty()) {
                System.out.println("Réponse GET /personInfo - Aucune personne trouvée avec le nom : " + lastName);
                return ResponseEntity.ok(personsInfo); // Retourne une liste vide
            }

            System.out.println("Réponse GET /personInfo - " + personsInfo.size() +
                    " personne(s) trouvée(s) avec le nom : " + lastName);
            return ResponseEntity.ok(personsInfo);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des informations pour le nom " + lastName + " : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
