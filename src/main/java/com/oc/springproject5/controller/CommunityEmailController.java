package com.oc.springproject5.controller;

import com.oc.springproject5.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/communityEmail")
public class CommunityEmailController {

    @Autowired
    private PersonService personService;

    @GetMapping
    public List<String> getEmailsByCity(@RequestParam String city) {
        System.out.println("Requête GET /communityEmail - Récupération des emails pour la ville : " + city);

            List<String> emails = personService.getEmailsByCity(city);
            System.out.println("Réponse GET /communityEmail - " + emails.size() +
                    " email(s) trouvé(s) pour la ville : " + city);
            return emails;
    }
}