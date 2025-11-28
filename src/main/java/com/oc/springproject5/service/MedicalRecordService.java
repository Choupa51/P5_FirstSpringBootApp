package com.oc.springproject5.service;

import com.oc.springproject5.exception.AlreadyExistException;
import com.oc.springproject5.exception.NotFoundException;
import com.oc.springproject5.model.MedicalRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MedicalRecordService {

    @Autowired
    private DataService dataService;

    public List<MedicalRecord> getAllMedicalRecords() {
        System.out.println("Récupération de tous les dossiers médicaux");
        return dataService.getAllMedicalRecords();
    }

    public MedicalRecord addMedicalRecord(MedicalRecord medicalRecord) {
        System.out.println("Ajout d'un nouveau dossier médical : " + medicalRecord.getFirstName() + " " + medicalRecord.getLastName());

        List<MedicalRecord> medicalRecords = dataService.getAllMedicalRecords();

        // Vérification si le dossier médical existe déjà
        for (MedicalRecord m : medicalRecords) {
            if (m.getFirstName().equalsIgnoreCase(medicalRecord.getFirstName())
                    && m.getLastName().equalsIgnoreCase(medicalRecord.getLastName())) {
                System.err.println("Le dossier médical pour " + medicalRecord.getFirstName() + " " + medicalRecord.getLastName() + " existe déjà");
                throw new AlreadyExistException("Ce dossier médical existe déjà");
            }
        }

        medicalRecords.add(medicalRecord);
        dataService.saveMedicalRecords(medicalRecords);

        System.out.println("Dossier médical ajouté avec succès : " + medicalRecord.getFirstName() + " " + medicalRecord.getLastName());
        return medicalRecord;
    }

    public MedicalRecord updateMedicalRecord(String firstName, String lastName, MedicalRecord updatedMedicalRecord) {
        System.out.println("Mise à jour du dossier médical : " + firstName + " " + lastName);

        List<MedicalRecord> medicalRecords = dataService.getAllMedicalRecords();
        MedicalRecord medicalRecordToUpdate = null;

        // Recherche du dossier médical à modifier
        medicalRecordToUpdate = medicalRecords.stream()
                .filter(m -> m.getFirstName().equalsIgnoreCase(firstName)
                        && m.getLastName().equalsIgnoreCase(lastName))
                .findFirst()
                .orElse(null);

        if (medicalRecordToUpdate == null) {
            System.err.println("Dossier médical non trouvé : " + firstName + " " + lastName);
            throw new NotFoundException("Dossier médical non trouvé");
        }

        // Mise à jour des champs (firstName et lastName ne changent pas selon les specs)
        if (updatedMedicalRecord.getBirthdate() != null) {
            medicalRecordToUpdate.setBirthdate(updatedMedicalRecord.getBirthdate());
        }
        if (updatedMedicalRecord.getMedications() != null) {
            medicalRecordToUpdate.setMedications(updatedMedicalRecord.getMedications());
        }
        if (updatedMedicalRecord.getAllergies() != null) {
            medicalRecordToUpdate.setAllergies(updatedMedicalRecord.getAllergies());
        }

        dataService.saveMedicalRecords(medicalRecords);

        System.out.println("Dossier médical mis à jour avec succès : " + medicalRecordToUpdate.getFirstName() + " " + medicalRecordToUpdate.getLastName());
        return medicalRecordToUpdate;
    }

    public void deleteMedicalRecord(String firstName, String lastName) {
        System.out.println("Suppression du dossier médical : " + firstName + " " + lastName);

        List<MedicalRecord> medicalRecords = dataService.getAllMedicalRecords();
        List<MedicalRecord> updatedMedicalRecords = new ArrayList<>();
        boolean found = false;

        // Reconstruction de la liste sans le dossier médical à supprimer
        for (MedicalRecord m : medicalRecords) {
            if (!(m.getFirstName().equalsIgnoreCase(firstName)
                    && m.getLastName().equalsIgnoreCase(lastName))) {
                updatedMedicalRecords.add(m);
            } else {
                found = true;
            }
        }

        if (!found) {
            System.err.println("Dossier médical non trouvé pour suppression : " + firstName + " " + lastName);
            throw new NotFoundException("Dossier médical non trouvé");
        }

        dataService.saveMedicalRecords(updatedMedicalRecords);
        System.out.println("Dossier médical supprimé avec succès : " + firstName + " " + lastName);
    }
}