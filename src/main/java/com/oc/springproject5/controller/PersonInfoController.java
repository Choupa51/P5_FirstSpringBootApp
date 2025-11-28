package com.oc.springproject5.controller;

import com.oc.springproject5.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/personInfo")
public class PersonInfoController {

    @Autowired
    private PersonService personService;

    @GetMapping
    public List<Map<String, Object>> getPersonInfoByLastName(@RequestParam String lastName) {
        System.out.println("Requête GET /personInfo - Récupération des informations pour le nom : " + lastName);

        List<Map<String, Object>> personsInfo = personService.getPersonInfoByLastName(lastName);

        if (personsInfo.isEmpty()) {
            System.out.println("Réponse GET /personInfo - Aucune personne trouvée avec le nom : " + lastName);
        } else {
            System.out.println("Réponse GET /personInfo - " + personsInfo.size() +
                    " personne(s) trouvée(s) avec le nom : " + lastName);
        }

        return personsInfo;
    }
}
