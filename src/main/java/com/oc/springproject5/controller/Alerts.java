package com.oc.springproject5.controller;

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
    public ResponseEntity<?> getChildrenAddress(@RequestParam String address) {
        System.out.println("Requête GET /childAlert - Recherche d'enfants à l'adresse : " + address);

        try {
            List<Map<String, Object>> children = personService.getChildrenAddress(address);

            if (children.isEmpty()){
                System.out.println("Réponse GET /childAlert - Aucun enfant trouvé à l'adresse : " + address);
                return ResponseEntity.ok("");
            }

            System.out.println("Réponse GET /childAlert - " + children.size() + " enfant(s) trouvé(s) à l'adresse : " + address);
            return ResponseEntity.ok(children);
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche d'enfants à l'adresse " + address + " : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/phoneAlert")
    public ResponseEntity<List<String>> getPhoneNumbersByFirestation(@RequestParam String firestation) {
        System.out.println("Requête GET /phoneAlert - Récupération des numéros de téléphone pour la caserne : " + firestation);

        try {
            List<String> phoneNumbers = firestationService.getPhoneNumbersByFirestation(firestation);
            System.out.println("Réponse GET /phoneAlert - " + phoneNumbers.size() +
                    " numéro(s) de téléphone trouvé(s) pour la caserne : " + firestation);
            return ResponseEntity.ok(phoneNumbers);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des numéros de téléphone pour la caserne " +
                    firestation + " : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/fire")
    public ResponseEntity<Map<String, Object>> getResidentsByFirestationAddress(@RequestParam String address){
        System.out.println("Requête GET /fire - Récupération des habitants et caserne pour l'adresse : " + address);

        try {
            Map<String, Object> response = personService.getResidentsAndFirestationAddress(address);
            System.out.println("Réponse GET /fire - " +
                    ((java.util.List<?>) response.get("residents")).size() +
                    " habitant(s) trouvé(s) à l'adresse : " + address +
                    ", caserne : " + response.get("stationNumber"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des informations pour l'adresse " + address + " : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @GetMapping("/flood/stations")
    public ResponseEntity<Map<String, Object>> getHousesByStations(@RequestParam String stations) {
        System.out.println("Requête GET /flood/stations - Récupération des foyers pour les stations : " + stations);

        try {
            Map<String, Object> response = firestationService.getHousesByStations(stations);
            System.out.println("Réponse GET /flood/stations - " +
                    response.size() + " adresse(s) trouvée(s) pour les stations : " + stations);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des foyers pour les stations " + stations + " : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
