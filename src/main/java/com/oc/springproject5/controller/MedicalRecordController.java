package com.oc.springproject5.controller;

import com.oc.springproject5.model.MedicalRecord;
import com.oc.springproject5.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @GetMapping
    public List<MedicalRecord> getAllMedicalRecords() {
        System.out.println("Requête GET /medicalRecord - Récupération de tous les dossiers médicaux");

        List<MedicalRecord> medicalRecords = medicalRecordService.getAllMedicalRecords();
        System.out.println("Réponse GET /medicalRecord - " + medicalRecords.size() + " dossiers médicaux retournés");

        return medicalRecords;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalRecord addMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        System.out.println("Requête POST /medicalRecord - Ajout d'un dossier médical : " +
                medicalRecord.getFirstName() + " " + medicalRecord.getLastName());

        MedicalRecord addedMedicalRecord = medicalRecordService.addMedicalRecord(medicalRecord);
        System.out.println("Réponse POST /medicalRecord - Dossier médical ajouté avec succès");

        return addedMedicalRecord;
    }

    @PutMapping
    public MedicalRecord updateMedicalRecord(@RequestParam String firstName,
                                             @RequestParam String lastName,
                                             @RequestBody MedicalRecord medicalRecord) {
        System.out.println("Requête PUT /medicalRecord - Mise à jour du dossier médical : " + firstName + " " + lastName);

        MedicalRecord updatedMedicalRecord = medicalRecordService.updateMedicalRecord(firstName, lastName, medicalRecord);
        System.out.println("Réponse PUT /medicalRecord - Dossier médical mis à jour avec succès");

        return updatedMedicalRecord;
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMedicalRecord(@RequestParam String firstName,
                                    @RequestParam String lastName) {
        System.out.println("Requête DELETE /medicalRecord - Suppression du dossier médical : " + firstName + " " + lastName);

        medicalRecordService.deleteMedicalRecord(firstName, lastName);
        System.out.println("Réponse DELETE /medicalRecord - Dossier médical supprimé avec succès");
    }
}