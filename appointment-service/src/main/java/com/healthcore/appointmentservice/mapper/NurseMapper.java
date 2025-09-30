package com.healthcore.appointmentservice.mapper;

import com.healthcore.appointmentservice.dto.response.NurseResponseDTO;
import com.healthcore.appointmentservice.persistence.entity.Nurse;
import org.springframework.stereotype.Component;

@Component
public class NurseMapper {
    public NurseResponseDTO toResponseDTO(Nurse nurse) {
        if (nurse == null) {
            return null;
        }

        return NurseResponseDTO.fromEntity(nurse);
    }
}
