package com.healthcore.appointmentservice.service.validator;

import com.healthcore.appointmentservice.dto.PatientRequestDTO;
import com.healthcore.appointmentservice.exception.DocumentAlreadyExistsException;
import com.healthcore.appointmentservice.persistence.entity.Patient;
import com.healthcore.appointmentservice.persistence.repository.PatientRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PatientValidator {

    public void validateUpdateRequest(PatientRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Request não pode ser nulo");
        }
    }

    public void validateDocumentUniqueness(String document, Long currentPatientId, PatientRepository repository) {
        if (document != null && !document.trim().isEmpty()) {
            Optional<Patient> existingPatient = repository.findByDocument(document);
            if (existingPatient.isPresent() && !existingPatient.get().getId().equals(currentPatientId)) {
                throw new DocumentAlreadyExistsException("Documento já existe para outro paciente");
            }
        }
    }
}
