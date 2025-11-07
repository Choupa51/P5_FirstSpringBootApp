package com.oc.springproject5.controller;

import com.oc.springproject5.model.Firestation;
import com.oc.springproject5.service.FirestationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/firestation")
public class FirestationController {

    @Autowired
    private FirestationService firestationService;

    @GetMapping
    public ResponseEntity<?> handleFirestationGet(@RequestParam(required = false) String stationNumber) {
        if (stationNumber != null) {
            // Nouvelle fonctionnalité : personnes couvertes par la station
            System.out.println("Requête GET /firestation - Récupération des personnes couvertes par la station : " + stationNumber);

            try {
                Map<String, Object> response = firestationService.getPersonsCoveredByStation(stationNumber);
                System.out.println("Réponse GET /firestation - Station " + stationNumber + " : " +
                        ((List<?>) response.get("persons")).size() + " personnes (" +
                        response.get("adultCount") + " adultes, " + response.get("childCount") + " enfants)");
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                System.err.println("Erreur lors de la récupération des personnes pour la station " + stationNumber + " : " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            // Comportement existant : tous les mappings
            System.out.println("Requête GET /firestation - Récupération de tous les mappings caserne/adresse");

            try {
                List<Firestation> firestations = firestationService.getAllFirestations();
                System.out.println("Réponse GET /firestation - " + firestations.size() + " mappings retournés");
                return ResponseEntity.ok(firestations);
            } catch (Exception e) {
                System.err.println("Erreur lors de la récupération des mappings : " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @PostMapping
    public ResponseEntity<Firestation> addFirestation(@RequestBody Firestation firestation) {
        System.out.println("Requête POST /firestation - Ajout d'un mapping : " +
                firestation.getAddress() + " -> station " + firestation.getStation());

        try {
            Firestation addedFirestation = firestationService.addFirestation(firestation);
            System.out.println("Réponse POST /firestation - Mapping ajouté avec succès");
            return ResponseEntity.status(HttpStatus.CREATED).body(addedFirestation);
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur lors de l'ajout du mapping : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout du mapping : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    public ResponseEntity<Firestation> updateFirestation(@RequestParam String address,
                                                         @RequestBody Firestation firestation) {
        System.out.println("Requête PUT /firestation - Mise à jour du mapping pour l'adresse : " + address);

        try {
            Firestation updatedFirestation = firestationService.updateFirestation(address, firestation);
            System.out.println("Réponse PUT /firestation - Mapping mis à jour avec succès");
            return ResponseEntity.ok(updatedFirestation);
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur lors de la mise à jour du mapping : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du mapping : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFirestation(@RequestParam(required = false) String address,
                                                  @RequestParam(required = false) String station) {

        if (address != null && station != null) {
            System.err.println("Erreur : Vous ne pouvez spécifier qu'un seul paramètre (address OU station)");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (address == null && station == null) {
            System.err.println("Erreur : Vous devez spécifier soit une adresse soit un numéro de station");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            if (address != null) {
                System.out.println("Requête DELETE /firestation - Suppression du mapping pour l'adresse : " + address);
                firestationService.deleteFirestationByAddress(address);
                System.out.println("Réponse DELETE /firestation - Mapping supprimé avec succès pour l'adresse");
            } else {
                System.out.println("Requête DELETE /firestation - Suppression de tous les mappings pour la station : " + station);
                firestationService.deleteFirestationByStation(station);
                System.out.println("Réponse DELETE /firestation - Mappings supprimés avec succès pour la station");
            }

            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur lors de la suppression du mapping : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression du mapping : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
