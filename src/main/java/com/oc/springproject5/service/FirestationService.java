package com.oc.springproject5.service;

import com.oc.springproject5.model.Firestation;
import com.oc.springproject5.model.MedicalRecord;
import com.oc.springproject5.model.Person;
import com.oc.springproject5.model.PersonInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FirestationService {

    @Autowired
    private DataService dataService;

    public List<Firestation> getAllFirestations() {
        System.out.println("Récupération de toutes les casernes");
        return dataService.getAllFirestations();
    }

    public Firestation addFirestation(Firestation firestation) {
        System.out.println("Ajout d'un nouveau mapping caserne/adresse : " + firestation.getAddress() + " -> station " + firestation.getStation());

        List<Firestation> firestations = dataService.getAllFirestations();

        // Vérification si le mapping existe déjà
        for (Firestation f : firestations) {
            if (f.getAddress().equalsIgnoreCase(firestation.getAddress())) {
                System.err.println("Un mapping existe déjà pour l'adresse : " + firestation.getAddress());
                throw new IllegalArgumentException("Un mapping existe déjà pour cette adresse");
            }
        }

        firestations.add(firestation);
        dataService.saveFirestations(firestations);

        System.out.println("Mapping caserne/adresse ajouté avec succès : " + firestation.getAddress() + " -> station " + firestation.getStation());
        return firestation;
    }

    public Firestation updateFirestation(String address, Firestation updatedFirestation) {
        System.out.println("Mise à jour du mapping pour l'adresse : " + address);

        List<Firestation> firestations = dataService.getAllFirestations();
        Firestation firestationToUpdate = null;

        // Recherche du mapping à modifier par adresse
        for (Firestation f : firestations) {
            if (f.getAddress().equalsIgnoreCase(address)) {
                firestationToUpdate = f;
                break;
            }
        }

        if (firestationToUpdate == null) {
            System.err.println("Aucun mapping trouvé pour l'adresse : " + address);
            throw new IllegalArgumentException("Aucun mapping trouvé pour cette adresse");
        }

        // Mise à jour du numéro de station
        if (updatedFirestation.getStation() != null) {
            firestationToUpdate.setStation(updatedFirestation.getStation());
        }

        dataService.saveFirestations(firestations);

        System.out.println("Mapping mis à jour avec succès : " + firestationToUpdate.getAddress() + " -> station " + firestationToUpdate.getStation());
        return firestationToUpdate;
    }

    public void deleteFirestationByAddress(String address) {
        System.out.println("Suppression du mapping pour l'adresse : " + address);

        List<Firestation> firestations = dataService.getAllFirestations();
        List<Firestation> updatedFirestations = new ArrayList<>();
        boolean found = false;

        // Reconstruction de la liste sans le mapping à supprimer
        for (Firestation f : firestations) {
            if (!f.getAddress().equalsIgnoreCase(address)) {
                updatedFirestations.add(f);
            } else {
                found = true;
            }
        }

        if (!found) {
            System.err.println("Aucun mapping trouvé pour suppression à l'adresse : " + address);
            throw new IllegalArgumentException("Aucun mapping trouvé pour cette adresse");
        }

        dataService.saveFirestations(updatedFirestations);
        System.out.println("Mapping supprimé avec succès pour l'adresse : " + address);
    }

    public void deleteFirestationByStation(String station) {
        System.out.println("Suppression de tous les mappings pour la station : " + station);

        List<Firestation> firestations = dataService.getAllFirestations();
        List<Firestation> updatedFirestations = new ArrayList<>();
        boolean found = false;

        // Reconstruction de la liste sans les mappings de la station à supprimer
        for (Firestation f : firestations) {
            if (!f.getStation().equalsIgnoreCase(station)) {
                updatedFirestations.add(f);
            } else {
                found = true;
            }
        }

        if (!found) {
            System.err.println("Aucun mapping trouvé pour suppression de la station : " + station);
            throw new IllegalArgumentException("Aucun mapping trouvé pour cette station");
        }

        dataService.saveFirestations(updatedFirestations);
        System.out.println("Tous les mappings supprimés avec succès pour la station : " + station);
    }

    public Map<String, Object> getPersonsCoveredByStation(String stationNumber) {
        System.out.println("Recherche des personnes couvertes par la station : " + stationNumber);

        // 1. Récupérer toutes les adresses couvertes par cette station
        List<String> addresses = dataService.getAddressesByStationNumber(stationNumber);

        if (addresses.isEmpty()) {
            System.out.println("Aucune adresse trouvée pour la station : " + stationNumber);
            Map<String, Object> emptyResponse = new HashMap<>();
            emptyResponse.put("persons", new ArrayList<>());
            emptyResponse.put("adultCount", 0);
            emptyResponse.put("childCount", 0);
            return emptyResponse;
        }

        List<Person> persons = dataService.getPersonsByAddresses(addresses);

        // 3. Créer la réponse avec les informations demandées
        List<PersonInfo> personInfos = new ArrayList<>();
        int adultCount = 0;
        int childCount = 0;

        for (Person person : persons) {
            // Créer les informations de la personne
            PersonInfo personInfo = new PersonInfo(
                    person.getFirstName(),
                    person.getLastName(),
                    person.getAddress(),
                    person.getPhone()
            );
            personInfos.add(personInfo);

            // Déterminer si adulte ou enfant
            if (isAdult(person.getFirstName(), person.getLastName())) {
                adultCount++;
            } else {
                childCount++;
            }
        }


        //À SUPPRIMER - Exemple d'utilisation de Stream
        List<String> prenoms = persons.stream().filter(p -> isAdult(p.getFirstName(), p.getLastName())).map(p -> p.getFirstName()).collect(Collectors.toList());




        System.out.println("Station " + stationNumber + " couvre " + persons.size() +
                " personnes (" + adultCount + " adultes, " + childCount + " enfants)");

        // Créer réponse finale
        Map<String, Object> response = new HashMap<>();
        response.put("persons", personInfos);
        response.put("adultCount", adultCount);
        response.put("childCount", childCount);

        return response;
    }

    private boolean isAdult(String firstName, String lastName) {
        // Date de naissance
        MedicalRecord record = dataService.getMedicalRecordByName(firstName, lastName);

        if (record != null) {
            return calculateAge(record.getBirthdate()) > 18;
        }

        // Si pas de dossier médical trouvé :
        System.out.println("Aucun dossier médical trouvé pour " + firstName + " " + lastName + ", considéré comme adulte");
        return true;
    }

    private int calculateAge(String birthdate) {
        try {
            // Parser la date de naissance (format MM/dd/yyyy)
            String[] parts = birthdate.split("/");
            int month = Integer.parseInt(parts[0]);
            int day = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            // Obtenir la date actuelle
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalDate birth = java.time.LocalDate.of(year, month, day);

            // Calculer l'âge
            return java.time.Period.between(birth, today).getYears();
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul de l'âge pour la date : " + birthdate + " - " + e.getMessage());
            return 25; // âge par défaut si erreur de parsing
        }
    }




    public List<String> getPhoneNumbersByFirestation(String stationNumber) {
        System.out.println("Recherche des numéros de téléphone pour la station : " + stationNumber);

        // 1. Récupérer toutes les adresses couvertes par cette station
        List<String> addresses = dataService.getAddressesByStationNumber(stationNumber);

        if (addresses.isEmpty()) {
            System.out.println("Aucune adresse trouvée pour la station : " + stationNumber);
            return new ArrayList<>();
        }

        // 2. Récupérer toutes les personnes habitant ces adresses
        List<Person> persons = dataService.getPersonsByAddresses(addresses);

        // 3. Extraire les numéros de téléphone uniques
        List<String> phoneNumbers = new ArrayList<>();
        for (Person person : persons) {
            String phone = person.getPhone();
            if (phone != null && !phone.trim().isEmpty() && !phoneNumbers.contains(phone)) {
                phoneNumbers.add(phone);
            }
        }

        System.out.println("Station " + stationNumber + " : " + phoneNumbers.size() +
                " numéro(s) de téléphone unique(s) trouvé(s) pour " + persons.size() + " personne(s)");

        return phoneNumbers;
    }

    public Map<String, Object> getHousesByStations(String stationsParam) {
        System.out.println("Recherche des foyers pour les stations : " + stationsParam);

        // 1. Parser la liste des numéros de stations
        String[] stationNumbers = stationsParam.split(",");

        // 2. Créer la réponse regroupée par adresse
        Map<String, Object> response = new HashMap<>();

        for (String stationNumber : stationNumbers) {
            String trimmedStation = stationNumber.trim();
            System.out.println("Traitement de la station : " + trimmedStation);

            // 3. Récupérer toutes les adresses pour cette station
            List<String> addresses = dataService.getAddressesByStationNumber(trimmedStation);

            for (String address : addresses) {
                // Éviter les doublons d'adresses si plusieurs stations desservent la même adresse
                if (!response.containsKey(address)) {
                    // 4. Récupérer toutes les personnes à cette adresse
                    List<Person> personsAtAddress = dataService.getPersonsByAddress(address);

                    // 5. Créer les informations détaillées pour chaque personne
                    List<Map<String, Object>> household = new ArrayList<>();

                    for (Person person : personsAtAddress) {
                        Map<String, Object> resident = new HashMap<>();

                        // Informations de base
                        resident.put("firstName", person.getFirstName());
                        resident.put("lastName", person.getLastName());
                        resident.put("phone", person.getPhone());

                        // Âge
                        int age = calculateAge(getPersonBirthdate(person.getFirstName(), person.getLastName()));
                        resident.put("age", age);

                        // Antécédents médicaux
                        MedicalRecord medicalRecord = dataService.getMedicalRecordByName(person.getFirstName(), person.getLastName());
                        if (medicalRecord != null) {
                            resident.put("medications", medicalRecord.getMedications());
                            resident.put("allergies", medicalRecord.getAllergies());
                        } else {
                            resident.put("medications", new ArrayList<String>());
                            resident.put("allergies", new ArrayList<String>());
                        }

                        household.add(resident);
                    }

                    response.put(address, household);
                    System.out.println("Adresse " + address + " : " + household.size() + " personne(s)");
                }
            }
        }

        System.out.println("Total : " + response.size() + " adresse(s) avec des foyers pour les stations : " + stationsParam);
        return response;
    }

    private String getPersonBirthdate(String firstName, String lastName) {
        MedicalRecord record = dataService.getMedicalRecordByName(firstName, lastName);

        if (record != null) {
            return record.getBirthdate();
        }

        // Si pas de dossier médical trouvé, retourner une date par défaut (adulte)
        return "01/01/1990";
    }



}