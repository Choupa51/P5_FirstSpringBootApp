package com.oc.springproject5.controller;

import com.oc.springproject5.exception.InvalideParameterException;
import com.oc.springproject5.model.Firestation;
import com.oc.springproject5.service.FirestationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/firestation")
public class FirestationController {

    @Autowired
    private FirestationService firestationService;

    @GetMapping
    public Object handleFirestationGet(@RequestParam(required = false) String stationNumber) {
        if (stationNumber != null) {
            System.out.println("Requête GET /firestation - Récupération des personnes couvertes par la station : " + stationNumber);

            Map<String, Object> response = firestationService.getPersonsCoveredByStation(stationNumber);
            System.out.println("Réponse GET /firestation - Station " + stationNumber + " : " +
                    ((List<?>) response.get("persons")).size() + " personnes (" +
                    response.get("adultCount") + " adultes, " + response.get("childCount") + " enfants)");

            return response;
        } else {
            System.out.println("Requête GET /firestation - Récupération de tous les mappings caserne/adresse");

            List<Firestation> firestations = firestationService.getAllFirestations();
            System.out.println("Réponse GET /firestation - " + firestations.size() + " mappings retournés");

            return firestations;
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Firestation addFirestation(@RequestBody Firestation firestation) {
        System.out.println("Requête POST /firestation - Ajout d'un mapping : " +
                firestation.getAddress() + " -> station " + firestation.getStation());

        Firestation addedFirestation = firestationService.addFirestation(firestation);
        System.out.println("Réponse POST /firestation - Mapping ajouté avec succès");

        return addedFirestation;
    }

    @PutMapping
    public Firestation updateFirestation(@RequestParam String address,
                                         @RequestBody Firestation firestation) {
        System.out.println("Requête PUT /firestation - Mise à jour du mapping pour l'adresse : " + address);

        Firestation updatedFirestation = firestationService.updateFirestation(address, firestation);
        System.out.println("Réponse PUT /firestation - Mapping mis à jour avec succès");

        return updatedFirestation;
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFirestation(@RequestParam(required = false) String address,
                                  @RequestParam(required = false) String station) {

        if (address != null && station != null) {
            throw new InvalideParameterException("Vous ne pouvez spécifier qu'un seul paramètre (address OU station)");
        }

        if (address == null && station == null) {
            throw new InvalideParameterException("Vous devez spécifier soit une adresse soit un numéro de station");
        }

        if (address != null) {
            System.out.println("Requête DELETE /firestation - Suppression du mapping pour l'adresse : " + address);
            firestationService.deleteFirestationByAddress(address);
            System.out.println("Réponse DELETE /firestation - Mapping supprimé avec succès pour l'adresse");
        } else {
            System.out.println("Requête DELETE /firestation - Suppression de tous les mappings pour la station : " + station);
            firestationService.deleteFirestationByStation(station);
            System.out.println("Réponse DELETE /firestation - Mappings supprimés avec succès pour la station");
        }
    }
}
