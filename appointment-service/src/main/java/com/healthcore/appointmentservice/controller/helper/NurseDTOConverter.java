package com.healthcore.appointmentservice.controller.helper;

import com.healthcore.appointmentservice.dto.NurseRequestDTO;
import com.healthcore.appointmentservice.dto.response.NurseResponseDTO;
import com.healthcore.appointmentservice.dto.update.NurseUpdateDTO;
import com.healthcore.appointmentservice.persistence.entity.Nurse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NurseDTOConverter {

    public NurseRequestDTO convertUpdateToRequest(Long nurseId, NurseUpdateDTO updateDTO) {
        return new NurseRequestDTO(
                nurseId,
                null,
                updateDTO.name(),
                updateDTO.coren()
        );
    }

    public List<NurseResponseDTO> convertToResponseList(List<Nurse> nurses) {
        return nurses.stream()
                .map(NurseResponseDTO::fromEntity)
                .toList();
    }

    public List<NurseResponseDTO> convertPageToResponseList(Page<Nurse> nursesPage) {
        return nursesPage.getContent().stream()
                .map(NurseResponseDTO::fromEntity)
                .toList();
    }
}
