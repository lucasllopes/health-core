package com.healthcore.appointmentservice.mapper;

import com.healthcore.appointmentservice.dto.response.DoctorResponseDTO;
import com.healthcore.appointmentservice.dto.response.PatientResponseDTO;
import com.healthcore.appointmentservice.persistence.entity.Doctor;
import com.healthcore.appointmentservice.persistence.entity.Patient;
import org.springframework.stereotype.Component;


@Component
public class DoctorMapper {
    public DoctorResponseDTO toResponseDTO(Doctor doctor) {
        if (doctor == null) {
            return null;
        }
        
        return DoctorResponseDTO.fromEntity(doctor);
    }
}
