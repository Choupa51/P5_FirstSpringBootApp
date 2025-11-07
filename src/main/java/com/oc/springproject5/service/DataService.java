package com.oc.springproject5.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oc.springproject5.model.Firestation;
import com.oc.springproject5.model.MedicalRecord;
import com.oc.springproject5.model.Person;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataService {

    private ObjectMapper objectMapper = new ObjectMapper();
    private List<Person> persons = new ArrayList<>();
    private List<Firestation> firestations = new ArrayList<>();
    private List<MedicalRecord> medicalRecords = new ArrayList<>();

    @PostConstruct
    public void loadData() {
        try {
            ClassPathResource resource = new ClassPathResource("data.json");
            JsonNode rootNode = objectMapper.readTree(resource.getInputStream());

            // Données des personnes

            JsonNode personsNode = rootNode.get("persons");
            for (JsonNode personNode : personsNode) {
                persons.add(objectMapper.readValue(personNode.toString(), Person.class));
            }

            // Données des casernes
            JsonNode firestationsNode = rootNode.get("firestations");
            for (JsonNode firestationNode : firestationsNode) {
                firestations.add(objectMapper.readValue(firestationNode.toString(), Firestation.class));
            }


            // Données médicales
            JsonNode medicalRecordsNode = rootNode.get("medicalrecords");
            for (JsonNode medicalNode : medicalRecordsNode) {
                // Conversion médicaments
                List<String> medications = new ArrayList<>();
                JsonNode medicationsNode = medicalNode.get("medications");
                if (medicationsNode.isArray()) {
                    for (JsonNode medicationNode : medicationsNode) {
                        medications.add(medicationNode.asText());
                    }
                }

                // Conversion allergies
                List<String> allergies = new ArrayList<>();
                JsonNode allergiesNode = medicalNode.get("allergies");
                if (allergiesNode.isArray()) {
                    for (JsonNode allergyNode : allergiesNode) {
                        allergies.add(allergyNode.asText());
                    }
                }

                MedicalRecord medicalRecord = new MedicalRecord(
                        medicalNode.get("firstName").asText(),
                        medicalNode.get("lastName").asText(),
                        medicalNode.get("birthdate").asText(),
                        medications,
                        allergies
                );

                medicalRecords.add(medicalRecord);
            }

            System.out.println("Données chargées avec succès : " + persons.size() + " personnes");
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement des données : " + e.getMessage());
            throw new RuntimeException("Impossible de charger les données", e);
        }
    }

    public List<Person> getAllPersons() {
        return persons;
    }

    public void savePersons(List<Person> persons) {
        this.persons = persons;
    }

    public List<Firestation> getAllFirestations() {
        return firestations;
    }

    public void saveFirestations(List<Firestation> firestations) {
        this.firestations = firestations;
    }

    public List<MedicalRecord> getAllMedicalRecords() {
        return medicalRecords;
    }

    public void saveMedicalRecords(List<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

    public List<String> getAddressesByStationNumber(String stationNumber) {
        List<String> addresses = new ArrayList<>();
        for (Firestation firestation : firestations) {
            if (firestation.getStation().equals(stationNumber)) {
                addresses.add(firestation.getAddress());
            }
        }
        return addresses;
    }

    public List<Person> getPersonsByAddresses(List<String> addresses) {
        List<Person> result = new ArrayList<>();
        for (Person person : persons) {
            if (addresses.contains(person.getAddress())) {
                result.add(person);
            }
        }
        return result;
    }

    public MedicalRecord getMedicalRecordByName(String firstName, String lastName) {
        for (MedicalRecord record : medicalRecords) {
            if (record.getFirstName().equalsIgnoreCase(firstName)
                    && record.getLastName().equalsIgnoreCase(lastName)) {
                return record;
            }
        }
        return null;
    }

    public List<Person> getPersonsByAddress(String address) {
        List<Person> result = new ArrayList<>();
        for (Person person : persons) {
            if (person.getAddress().equalsIgnoreCase(address)) {
                result.add(person);
            }
        }

        return result;
    }


    public String getFirestationNumberAddress(String address) {
        for (Firestation firestation : firestations) {
            if (firestation.getAddress().equalsIgnoreCase(address)) {
                return firestation.getStation();
            }
        }
        return null;
    }


    public List<Person> getPersonsByLastName(String lastName) {
        List<Person> result = new ArrayList<>();
        for (Person person : persons) {
            if (person.getLastName().equalsIgnoreCase(lastName)) {
                result.add(person);
            }
        }
        return result;
    }

    public List<Person> getPersonsByCity(String city) {
        List<Person> result = new ArrayList<>();
        for (Person person : persons) {
            if (person.getCity().equalsIgnoreCase(city)) {
                result.add(person);
            }
        }
        return result;
    }

}

