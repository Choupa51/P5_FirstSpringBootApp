package com.oc.springproject5.controller;

import com.oc.springproject5.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/communityEmail")
public class CommunityEmailController {

    @Autowired
    private PersonService personService;

    @GetMapping
    public ResponseEntity<List<String>> getEmailsByCity(@RequestParam String city) {
        System.out.println("Requête GET /communityEmail - Récupération des emails pour la ville : " + city);

        try {
            List<String> emails = personService.getEmailsByCity(city);
            System.out.println("Réponse GET /communityEmail - " + emails.size() +
                    " email(s) trouvé(s) pour la ville : " + city);
            return ResponseEntity.ok(emails);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des emails pour la ville " + city + " : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}