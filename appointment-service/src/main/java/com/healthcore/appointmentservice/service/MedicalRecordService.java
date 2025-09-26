package com.healthcore.appointmentservice.service;

import com.healthcore.appointmentservice.dto.CreateMedicalRecordRequestDTO;
import com.healthcore.appointmentservice.dto.UpdateMedicalRecordRequestDTO;
import com.healthcore.appointmentservice.dto.MedicalRecordResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MedicalRecordService {
    MedicalRecordResponseDTO create(CreateMedicalRecordRequestDTO createMedicalRecordRequestDTO);
    Page<MedicalRecordResponseDTO> getAll(Pageable pageable);
    Optional<MedicalRecordResponseDTO> getById(Long id);
    MedicalRecordResponseDTO update(Long id, UpdateMedicalRecordRequestDTO updateMedicalRecordRequestDTO);
    void delete(Long id);
}

