package com.oc.springproject5.controller;

import com.oc.springproject5.model.MedicalRecord;
import com.oc.springproject5.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @GetMapping
    public ResponseEntity<List<MedicalRecord>> getAllMedicalRecords() {
        System.out.println("Requête GET /medicalRecord - Récupération de tous les dossiers médicaux");

        try {
            List<MedicalRecord> medicalRecords = medicalRecordService.getAllMedicalRecords();
            System.out.println("Réponse GET /medicalRecord - " + medicalRecords.size() + " dossiers médicaux retournés");
            return ResponseEntity.ok(medicalRecords);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des dossiers médicaux : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<MedicalRecord> addMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        System.out.println("Requête POST /medicalRecord - Ajout d'un dossier médical : " +
                medicalRecord.getFirstName() + " " + medicalRecord.getLastName());

        try {
            MedicalRecord addedMedicalRecord = medicalRecordService.addMedicalRecord(medicalRecord);
            System.out.println("Réponse POST /medicalRecord - Dossier médical ajouté avec succès");
            return ResponseEntity.status(HttpStatus.CREATED).body(addedMedicalRecord);
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur lors de l'ajout du dossier médical : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout du dossier médical : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    public ResponseEntity<MedicalRecord> updateMedicalRecord(@RequestParam String firstName,
                                                             @RequestParam String lastName,
                                                             @RequestBody MedicalRecord medicalRecord) {
        System.out.println("Requête PUT /medicalRecord - Mise à jour du dossier médical : " + firstName + " " + lastName);

        try {
            MedicalRecord updatedMedicalRecord = medicalRecordService.updateMedicalRecord(firstName, lastName, medicalRecord);
            System.out.println("Réponse PUT /medicalRecord - Dossier médical mis à jour avec succès");
            return ResponseEntity.ok(updatedMedicalRecord);
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur lors de la mise à jour du dossier médical : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du dossier médical : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMedicalRecord(@RequestParam String firstName,
                                                    @RequestParam String lastName) {
        System.out.println("Requête DELETE /medicalRecord - Suppression du dossier médical : " + firstName + " " + lastName);

        try {
            medicalRecordService.deleteMedicalRecord(firstName, lastName);
            System.out.println("Réponse DELETE /medicalRecord - Dossier médical supprimé avec succès");
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur lors de la suppression du dossier médical : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression du dossier médical : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}