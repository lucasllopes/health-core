package com.healthcore.appointmentservice.controller.helper;

import com.healthcore.appointmentservice.dto.PatientRequestDTO;
import com.healthcore.appointmentservice.dto.response.PatientResponseDTO;
import com.healthcore.appointmentservice.dto.update.PatientUpdateDTO;
import com.healthcore.appointmentservice.persistence.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PatientDTOConverter {

    public PatientRequestDTO convertUpdateToRequest(Long patientId, PatientUpdateDTO updateDTO) {
        return new PatientRequestDTO(
                patientId,
                null,
                updateDTO.name(),
                updateDTO.dateOfBirth(),
                updateDTO.document(),
                updateDTO.phone(),
                updateDTO.email(),
                updateDTO.address()
        );
    }

    public List<PatientResponseDTO> convertToResponseList(List<Patient> patients) {
        return patients.stream()
                .map(PatientResponseDTO::fromEntity)
                .toList();
    }

    public List<PatientResponseDTO> convertPageToResponseList(Page<Patient> patientsPage) {
        return patientsPage.getContent().stream()
                .map(PatientResponseDTO::fromEntity)
                .toList();
    }
}