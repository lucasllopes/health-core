package com.healthcore.appointmentservice.controller.helper;

import com.healthcore.appointmentservice.dto.DoctorRequestDTO;
import com.healthcore.appointmentservice.dto.response.DoctorResponseDTO;
import com.healthcore.appointmentservice.dto.update.DoctorUpdateDTO;
import com.healthcore.appointmentservice.persistence.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DoctorDTOConverter {

    public DoctorRequestDTO convertUpdateToRequest(Long doctorId, DoctorUpdateDTO updateDTO) {
        return new DoctorRequestDTO(
                doctorId,
                null,
                updateDTO.name(),
                updateDTO.specialty(),
                updateDTO.crm()
        );
    }

    public List<DoctorResponseDTO> convertToResponseList(List<Doctor> doctors) {
        return doctors.stream()
                .map(DoctorResponseDTO::fromEntity)
                .toList();
    }

    public List<DoctorResponseDTO> convertPageToResponseList(Page<Doctor> doctorsPage) {
        return doctorsPage.getContent().stream()
                .map(DoctorResponseDTO::fromEntity)
                .toList();
    }
}