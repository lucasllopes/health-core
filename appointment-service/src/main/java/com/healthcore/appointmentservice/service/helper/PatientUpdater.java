package com.healthcore.appointmentservice.service.helper;

import com.healthcore.appointmentservice.dto.PatientRequestDTO;
import com.healthcore.appointmentservice.persistence.entity.Patient;
import com.healthcore.appointmentservice.persistence.repository.PatientRepository;
import com.healthcore.appointmentservice.service.validator.PatientValidator;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PatientUpdater {

    private final PatientValidator validator;
    private final PatientRepository patientRepository;

    public PatientUpdater(PatientValidator validator, PatientRepository patientRepository) {
        this.validator = validator;
        this.patientRepository = patientRepository;
    }

    public void updatePatientFields(Patient patient, PatientRequestDTO request, Long patientId) {
        updateNameIfPresent(patient, request.name());
        updateDateOfBirthIfPresent(patient, request.dateOfBirth());
        updateDocumentIfPresent(patient, request.document(), patientId);
        updatePhoneIfPresent(patient, request.phone());
        updateEmailIfPresent(patient, request.email());
        updateAddressIfPresent(patient, request.address());
    }

    private void updateNameIfPresent(Patient patient, String name) {
        if (isValidString(name)) {
            patient.setName(name);
        }
    }

    private void updateDateOfBirthIfPresent(Patient patient, LocalDate dateOfBirth) {
        if (dateOfBirth != null) {
            patient.setDateOfBirth(dateOfBirth);
        }
    }

    private void updateDocumentIfPresent(Patient patient, String document, Long patientId) {
        if (isValidString(document)) {
            validator.validateDocumentUniqueness(document, patientId, patientRepository);
            patient.setDocument(document);
        }
    }

    private void updatePhoneIfPresent(Patient patient, String phone) {
        if (phone != null) {
            patient.setPhone(phone);
        }
    }

    private void updateEmailIfPresent(Patient patient, String email) {
        if (email != null) {
            patient.setEmail(email);
        }
    }

    private void updateAddressIfPresent(Patient patient, String address) {
        if (address != null) {
            patient.setAddress(address);
        }
    }

    private boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
