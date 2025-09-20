package com.healthcore.appointmentservice.service.validator;

import com.healthcore.appointmentservice.dto.DoctorRequestDTO;
import com.healthcore.appointmentservice.exception.CRMAlreadyExistsException;
import com.healthcore.appointmentservice.persistence.entity.Doctor;
import com.healthcore.appointmentservice.persistence.repository.DoctorRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DoctorValidator {

    public void validateUpdateRequest(DoctorRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Request não pode ser nulo");
        }
    }

    public void validateCRMUniqueness(String crm, Long currentDoctorId, DoctorRepository repository) {
        if (crm != null && !crm.trim().isEmpty()) {
            Optional<Doctor> existingDoctor = repository.findByCrm(crm);
            if (existingDoctor.isPresent() && !existingDoctor.get().getId().equals(currentDoctorId)) {
                throw new CRMAlreadyExistsException("CRM já existe para outro médico");
            }
        }
    }
}
