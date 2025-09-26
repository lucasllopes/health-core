package com.healthcore.appointmentservice.controller;

import com.healthcore.appointmentservice.dto.CreateMedicalRecordRequestDTO;
import com.healthcore.appointmentservice.dto.UpdateMedicalRecordRequestDTO;
import com.healthcore.appointmentservice.dto.MedicalRecordResponseDTO;
import com.healthcore.appointmentservice.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicalrecords")
public class MedicalRecordController {
    private final Logger logger = LoggerFactory.getLogger(MedicalRecordController.class);
    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @PostMapping
    public ResponseEntity<MedicalRecordResponseDTO> createMedicalRecord(@Valid @RequestBody CreateMedicalRecordRequestDTO createMedicalRecordRequestDTO) {
        logger.info("Handling POST request to /medicalrecords");
        MedicalRecordResponseDTO response = medicalRecordService.create(createMedicalRecordRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MedicalRecordResponseDTO>> getAllMedicalRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        logger.info("Handling GET request to /medicalrecords - page: {}, size: {}, sortBy: {}, sortDirection: {}", page, size, sortBy, sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
        Page<MedicalRecordResponseDTO> recordsPage = medicalRecordService.getAll(pageable);
        return ResponseEntity.ok(recordsPage.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponseDTO> getMedicalRecordById(@PathVariable Long id) {
        logger.info("Handling GET request to /medicalrecords/{}", id);
        return medicalRecordService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordResponseDTO> updateMedicalRecord(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMedicalRecordRequestDTO updateMedicalRecordRequestDTO
    ) {
        logger.info("Handling PUT request to /medicalrecords/{}", id);
        MedicalRecordResponseDTO response = medicalRecordService.update(id, updateMedicalRecordRequestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable Long id) {
        logger.info("Handling DELETE request to /medicalrecords/{}", id);
        medicalRecordService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
