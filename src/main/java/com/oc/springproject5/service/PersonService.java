package com.oc.springproject5.service;

import com.oc.springproject5.model.MedicalRecord;
import com.oc.springproject5.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PersonService {

    @Autowired
    private DataService dataService;

    public List<Person> getAllPersons() {
        System.out.println("Récupération de toutes les personnes");
        return dataService.getAllPersons();
    }

    public Person addPerson(Person person) {
        System.out.println("Ajout d'une nouvelle personne : " + person.getFirstName() + " " + person.getLastName());

        List<Person> persons = dataService.getAllPersons();

        // Vérification si la personne existe déjà
        for (Person p : persons) {
            if (p.getFirstName().equalsIgnoreCase(person.getFirstName())
                    && p.getLastName().equalsIgnoreCase(person.getLastName())) {
                System.err.println("La personne " + person.getFirstName() + " " + person.getLastName() + " existe déjà");
                throw new IllegalArgumentException("Cette personne existe déjà");
            }
        }

        persons.add(person);
        dataService.savePersons(persons);

        System.out.println("Personne ajoutée avec succès : " + person.getFirstName() + " " + person.getLastName());
        return person;
    }

    public Person updatePerson(String firstName, String lastName, Person updatedPerson) {
        System.out.println("Mise à jour de la personne : " + firstName + " " + lastName);

        List<Person> persons = dataService.getAllPersons();
        Person personToUpdate = null;

        // Recherche de la personne à modifier
        for (Person p : persons) {
            if (p.getFirstName().equalsIgnoreCase(firstName)
                    && p.getLastName().equalsIgnoreCase(lastName)) {
                personToUpdate = p;
                break;
            }
        }

        if (personToUpdate == null) {
            System.err.println("Personne non trouvée : " + firstName + " " + lastName);
            throw new IllegalArgumentException("Personne non trouvée");
        }

        // Mise à jour des champs (firstName et lastName ne changent pas selon les specs)
        if (updatedPerson.getAddress() != null) {
            personToUpdate.setAddress(updatedPerson.getAddress());
        }
        if (updatedPerson.getCity() != null) {
            personToUpdate.setCity(updatedPerson.getCity());
        }
        if (updatedPerson.getZip() != null) {
            personToUpdate.setZip(updatedPerson.getZip());
        }
        if (updatedPerson.getPhone() != null) {
            personToUpdate.setPhone(updatedPerson.getPhone());
        }
        if (updatedPerson.getEmail() != null) {
            personToUpdate.setEmail(updatedPerson.getEmail());
        }

        dataService.savePersons(persons);

        System.out.println("Personne mise à jour avec succès : " + personToUpdate.getFirstName() + " " + personToUpdate.getLastName());
        return personToUpdate;
    }

    public void deletePerson(String firstName, String lastName) {
        System.out.println("Suppression de la personne : " + firstName + " " + lastName);

        List<Person> persons = dataService.getAllPersons();
        List<Person> updatedPersons = new ArrayList<>();
        boolean found = false;

        // Reconstruction de la liste sans la personne à supprimer
        for (Person p : persons) {
            if (!(p.getFirstName().equalsIgnoreCase(firstName)
                    && p.getLastName().equalsIgnoreCase(lastName))) {
                updatedPersons.add(p);
            } else {
                found = true;
            }
        }

        if (!found) {
            System.err.println("Personne non trouvée pour suppression : " + firstName + " " + lastName);
            throw new IllegalArgumentException("Personne non trouvée");
        }

        dataService.savePersons(updatedPersons);
        System.out.println("Personne supprimée avec succès : " + firstName + " " + lastName);
    }


    public List<Map<String, Object>> getChildrenAddress(String address) {
        System.out.println("Recherche d'enfants à l'adresse : " + address);

        // 1. Récupérer toutes les personnes à cette adresse
        List<Person> personsAtAddress = dataService.getPersonsByAddress(address);

        if (personsAtAddress.isEmpty()) {
            System.out.println("Aucune personne trouvée à l'adresse : " + address);
            return new ArrayList<>();
        }

        // 2. Identifier les enfants et les autres membres du foyer
        List<Map<String, Object>> children = new ArrayList<>();
        List<Map<String, String>> otherHouseholdMembers = new ArrayList<>();

        for (Person person : personsAtAddress) {
            int age = getPersonAge(person.getFirstName(), person.getLastName());

            Map<String, String> householdMember = new HashMap<>();
            householdMember.put("firstName", person.getFirstName());
            householdMember.put("lastName", person.getLastName());

            if (age <= 18) {
                // C'est un enfant
                Map<String, Object> child = new HashMap<>();
                child.put("firstName", person.getFirstName());
                child.put("lastName", person.getLastName());
                child.put("age", age);
                children.add(child);
            } else {
                // C'est un autre membre du foyer
                otherHouseholdMembers.add(householdMember);
            }
        }

        // 3. Ajouter la liste des autres membres du foyer à chaque enfant
        for (Map<String, Object> child : children) {
            child.put("householdMembers", new ArrayList<>(otherHouseholdMembers));
        }

        System.out.println("Trouvé " + children.size() + " enfant(s) et " +
                otherHouseholdMembers.size() + " autre(s) membre(s) du foyer à l'adresse : " + address);

        return children;
    }

    private int getPersonAge(String firstName, String lastName) {
        // Récupérer le dossier médical pour obtenir la date de naissance
        MedicalRecord record = dataService.getMedicalRecordByName(firstName, lastName);

        if (record != null) {
            return calculateAge(record.getBirthdate());
        }

        // Si pas de dossier médical trouvé, considérer comme adulte (âge > 18)
        System.out.println("Aucun dossier médical trouvé pour " + firstName + " " + lastName + ", âge par défaut : 25 ans");
        return 25;
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

    public Map<String, Object> getResidentsAndFirestationAddress(String address) {
        System.out.println("Recherche des habitants et caserne pour l'adresse : " + address);

        List<Person> personsAtAddress = dataService.getPersonsByAddress(address);
        String stationNumber = dataService.getFirestationNumberAddress(address);
        List<Map<String, Object>> residents = new ArrayList<>();

        for (Person person : personsAtAddress) {
            Map<String, Object> resident = new HashMap<>();

            // Informations de base
            resident.put("firstName", person.getFirstName());
            resident.put("lastName", person.getLastName());
            resident.put("phone", person.getPhone());

            // Âge (RÉUTILISE LA MÉTHODE EXISTANTE)
            int age = getPersonAge(person.getFirstName(), person.getLastName());
            resident.put("age", age);

            // Antécédents médicaux
            MedicalRecord medicalRecord = dataService.getMedicalRecordByName(person.getFirstName(), person.getLastName());
            if (medicalRecord != null) {
                resident.put("medications", medicalRecord.getMedications());
                resident.put("allergies", medicalRecord.getAllergies());
            } else {
                resident.put("medications", new ArrayList<String>());
                resident.put("allergies", new ArrayList<String>());
                System.out.println("Aucun dossier médical trouvé pour " + person.getFirstName() + " " + person.getLastName());
            }

            residents.add(resident);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("residents", residents);
        response.put("stationNumber", stationNumber != null ? stationNumber : "Aucune caserne assignée");

        System.out.println("Trouvé " + residents.size() + " habitant(s) à l'adresse : " + address +
                ", caserne : " + (stationNumber != null ? stationNumber : "non assignée"));

        return response;
    }


    public List<Map<String, Object>> getPersonInfoByLastName(String lastName) {
        System.out.println("Recherche des informations pour le nom : " + lastName);

        // 1. Récupérer toutes les personnes avec ce nom de famille
        List<Person> persons = dataService.getPersonsByLastName(lastName);

        if (persons.isEmpty()) {
            System.out.println("Aucune personne trouvée avec le nom : " + lastName);
            return new ArrayList<>();
        }

        // 2. Créer les informations détaillées pour chaque personne
        List<Map<String, Object>> personsInfo = new ArrayList<>();

        for (Person person : persons) {
            Map<String, Object> personInfo = new HashMap<>();

            // Informations de base
            personInfo.put("firstName", person.getFirstName());
            personInfo.put("lastName", person.getLastName());
            personInfo.put("address", person.getAddress());
            personInfo.put("email", person.getEmail());

            // Âge
            int age = getPersonAge(person.getFirstName(), person.getLastName());
            personInfo.put("age", age);

            // Antécédents médicaux
            MedicalRecord medicalRecord = dataService.getMedicalRecordByName(person.getFirstName(), person.getLastName());
            if (medicalRecord != null) {
                personInfo.put("medications", medicalRecord.getMedications());
                personInfo.put("allergies", medicalRecord.getAllergies());
            } else {
                personInfo.put("medications", new ArrayList<String>());
                personInfo.put("allergies", new ArrayList<String>());
                System.out.println("Aucun dossier médical trouvé pour " + person.getFirstName() + " " + person.getLastName());
            }

            personsInfo.add(personInfo);
        }

        System.out.println("Trouvé " + personsInfo.size() + " personne(s) avec le nom : " + lastName);
        return personsInfo;
    }

    public List<String> getEmailsByCity(String city) {
        System.out.println("Recherche des emails pour la ville : " + city);

        // 1. Récupérer toutes les personnes de cette ville
        List<Person> personsInCity = dataService.getPersonsByCity(city);

        if (personsInCity.isEmpty()) {
            System.out.println("Aucune personne trouvée dans la ville : " + city);
            return new ArrayList<>();
        }

        // 2. Extraire les emails uniques
        List<String> emails = new ArrayList<>();
        for (Person person : personsInCity) {
            String email = person.getEmail();
            if (email != null && !email.trim().isEmpty() && !emails.contains(email)) {
                emails.add(email);
            }
        }

        System.out.println("Trouvé " + emails.size() + " email(s) unique(s) pour " +
                personsInCity.size() + " personne(s) dans la ville : " + city);

        return emails;
    }

}