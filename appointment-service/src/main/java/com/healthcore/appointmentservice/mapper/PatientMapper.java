package com.healthcore.appointmentservice.mapper;

import com.healthcore.appointmentservice.dto.response.PatientResponseDTO;
import com.healthcore.appointmentservice.persistence.entity.Patient;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class PatientMapper {
    public PatientResponseDTO toResponseDTO(Patient patient) {
        if (patient == null) {
            return null;
        }
        
        return PatientResponseDTO.fromEntity(patient);
    }
}
