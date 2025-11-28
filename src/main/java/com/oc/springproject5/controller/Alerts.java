package com.oc.springproject5.controller;

import com.oc.springproject5.model.Person;
import com.oc.springproject5.service.FirestationService;
import com.oc.springproject5.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class Alerts {

    @Autowired
    private PersonService personService;

    @Autowired
    private FirestationService firestationService;

    @GetMapping("/childAlert")
    public List<Map<String, Object>> getChildrenAddress(@RequestParam String address) {
        System.out.println("Requête GET /childAlert - Recherche d'enfants à l'adresse : " + address);

        List<Map<String, Object>> children = personService.getChildrenAddress(address);

        if (children.isEmpty()) {
            System.out.println("Réponse GET /childAlert - Aucun enfant trouvé à l'adresse : " + address);
        } else {
            System.out.println("Réponse GET /childAlert - " + children.size() + " enfant(s) trouvé(s) à l'adresse : " + address);
        }

        return children;
    }

    @GetMapping("/phoneAlert")
    public List<String> getPhoneNumbersByFirestation(@RequestParam String firestation) {
        System.out.println("Requête GET /phoneAlert - Récupération des numéros de téléphone pour la caserne : " + firestation);

        List<String> phoneNumbers = firestationService.getPhoneNumbersByFirestation(firestation);
        System.out.println("Réponse GET /phoneAlert - " + phoneNumbers.size() +
                " numéro(s) de téléphone trouvé(s) pour la caserne : " + firestation);

        return phoneNumbers;
    }


    @GetMapping("/fire")
    public Map<String, Object> getResidentsByFirestationAddress(@RequestParam String address) {
        System.out.println("Requête GET /fire - Récupération des habitants et caserne pour l'adresse : " + address);

        Map<String, Object> response = personService.getResidentsAndFirestationAddress(address);
        System.out.println("Réponse GET /fire - " +
                ((java.util.List<?>) response.get("residents")).size() +
                " habitant(s) trouvé(s) à l'adresse : " + address +
                ", caserne : " + response.get("stationNumber"));

        return response;
    }



    @GetMapping("/flood/stations")
    public Map<String, Object> getHousesByStations(@RequestParam String stations) {
        System.out.println("Requête GET /flood/stations - Récupération des foyers pour les stations : " + stations);

        Map<String, Object> response = firestationService.getHousesByStations(stations);
        System.out.println("Réponse GET /flood/stations - " +
                response.size() + " adresse(s) trouvée(s) pour les stations : " + stations);

        return response;
    }

}
