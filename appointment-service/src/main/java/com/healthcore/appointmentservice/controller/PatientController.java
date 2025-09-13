package com.healthcore.appointmentservice.controller;

import com.healthcore.appointmentservice.dto.registration.PatientRegistrationDTO;
import com.healthcore.appointmentservice.dto.response.PatientResponseDTO;
import com.healthcore.appointmentservice.dto.update.PatientUpdateDTO;
import com.healthcore.appointmentservice.persistence.entity.Patient;
import com.healthcore.appointmentservice.service.PatientService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private static final Logger log = LoggerFactory.getLogger(PatientController.class);

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('NURSE') or hasRole('DOCTOR')")
    public ResponseEntity<PatientResponseDTO> createPatient(@Valid @RequestBody PatientRegistrationDTO dto) {
        log.info("Criando novo paciente: {}", dto.username());

        try {
            Patient patient = patientService.createPatient(dto);
            PatientResponseDTO response = PatientResponseDTO.fromEntity(patient);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("Erro ao criar paciente: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('NURSE') or hasRole('DOCTOR')")
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Buscando todos os pacientes - página: {}, tamanho: {}", page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Patient> patientsPage = patientService.getAllPatients(pageable);

        List<PatientResponseDTO> response = patientsPage.getContent().stream()
                .map(PatientResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NURSE') or hasRole('DOCTOR') or (hasRole('PATIENT') and @patientService.getPatientByUserId(authentication.principal.id).map(p -> p.id).orElse(-1L) == #id)")
    public ResponseEntity<PatientResponseDTO> getPatientById(@PathVariable Long id) {
        log.info("Buscando paciente por ID: {}", id);

        return patientService.getPatientById(id)
                .map(patient -> ResponseEntity.ok(PatientResponseDTO.fromEntity(patient)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NURSE') or hasRole('DOCTOR') or (hasRole('PATIENT') and @patientService.getPatientByUserId(authentication.principal.id).map(p -> p.id).orElse(-1L) == #id)")
    public ResponseEntity<PatientResponseDTO> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientUpdateDTO dto) {

        log.info("Atualizando paciente ID: {}", id);

        try {
            var requestDTO = new com.healthcore.appointmentservice.dto.PatientRequestDTO(
                    id, null, dto.name(), dto.dateOfBirth(),
                    dto.document(), dto.phone(), dto.email(), dto.address()
            );

            Patient updatedPatient = patientService.updatePatient(id, requestDTO);
            PatientResponseDTO response = PatientResponseDTO.fromEntity(updatedPatient);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Erro ao atualizar paciente: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<Void> disablePatient(@PathVariable Long id) {
        log.info("Desabilitando paciente ID: {}", id);

        try {
            patientService.disablePatient(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erro ao desabilitar paciente: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<Void> enablePatient(@PathVariable Long id) {
        log.info("Habilitando paciente ID: {}", id);

        try {
            patientService.enablePatient(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erro ao habilitar paciente: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

}