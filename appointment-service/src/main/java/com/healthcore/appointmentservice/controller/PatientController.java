package com.healthcore.appointmentservice.controller;

import com.healthcore.appointmentservice.dto.PatientRequestDTO;
import com.healthcore.appointmentservice.dto.registration.PatientRegistrationDTO;
import com.healthcore.appointmentservice.dto.response.PatientResponseDTO;
import com.healthcore.appointmentservice.dto.update.PatientUpdateDTO;
import com.healthcore.appointmentservice.persistence.entity.Patient;
import com.healthcore.appointmentservice.service.PatientService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.healthcore.appointmentservice.controller.helper.PatientDTOConverter;
import com.healthcore.appointmentservice.controller.helper.PaginationHelper;
import java.util.function.Supplier;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private static final Logger log = LoggerFactory.getLogger(PatientController.class);

    private static final String ADMIN_NURSE_DOCTOR_ROLES = "hasRole('ADMIN') or hasRole('NURSE') or hasRole('DOCTOR')";
    private static final String ADMIN_DOCTOR_ROLES = "hasRole('ADMIN') or hasRole('DOCTOR')";
    private static final String PATIENT_SELF_ACCESS = "hasRole('PATIENT') and @patientService.getPatientByUserId(authentication.principal.id).map(p -> p.id).orElse(-1L) == #id";

    private final PatientService patientService;
    private final PatientDTOConverter dtoConverter;
    private final PaginationHelper paginationHelper;

    public PatientController(PatientService patientService, PatientDTOConverter dtoConverter, PaginationHelper paginationHelper) {
        this.patientService = patientService;
        this.dtoConverter = dtoConverter;
        this.paginationHelper = paginationHelper;
    }

    @PostMapping
    @PreAuthorize(ADMIN_NURSE_DOCTOR_ROLES)
    public ResponseEntity<PatientResponseDTO> createPatient(
            @Valid @RequestBody PatientRegistrationDTO registrationRequest) {

        return handlePatientOperation(
                "Criando",
                null,
                () -> patientService.createPatient(registrationRequest)
        );
    }

    @GetMapping
    @PreAuthorize(ADMIN_NURSE_DOCTOR_ROLES)
    public ResponseEntity<List<PatientResponseDTO>> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        log.info("Buscando pacientes - página: {}, tamanho: {}, ordenação: {} {}",
                page, size, sortBy, sortDirection);

        Pageable pageable = paginationHelper.createPageable(page, size, sortBy, sortDirection);
        Page<Patient> patientsPage = patientService.getAllPatients(pageable);

        List<PatientResponseDTO> responseList = dtoConverter.convertPageToResponseList(patientsPage);

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    @PreAuthorize(ADMIN_NURSE_DOCTOR_ROLES + " or (" + PATIENT_SELF_ACCESS + ")")
    public ResponseEntity<PatientResponseDTO> getPatientById(@PathVariable Long id) {
        log.info("Buscando paciente por ID: {}", id);

        return patientService.getPatientById(id)
                .map(patient -> ResponseEntity.ok(PatientResponseDTO.fromEntity(patient)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @PreAuthorize(ADMIN_NURSE_DOCTOR_ROLES)
    public ResponseEntity<List<PatientResponseDTO>> searchPatients(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String document) {

        log.info("Buscando pacientes - nome: '{}', email: '{}', documento: '{}'",
                name, email, document);

        List<PatientResponseDTO> foundPatients = patientService.searchPatients(name, email, document);

        log.info("Encontrados {} pacientes para a busca", foundPatients.size());

        return ResponseEntity.ok(foundPatients);
    }

    @PutMapping("/{id}")
    @PreAuthorize(ADMIN_NURSE_DOCTOR_ROLES + " or (" + PATIENT_SELF_ACCESS + ")")
    public ResponseEntity<PatientResponseDTO> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientUpdateDTO updateRequest) {

        return handlePatientOperation(
                "Atualizando",
                id,
                () -> {
                    PatientRequestDTO requestDTO = dtoConverter.convertUpdateToRequest(id, updateRequest);
                    return patientService.updatePatient(id, requestDTO);
                }
        );
    }

    @PatchMapping("/{id}/disable")
    @PreAuthorize(ADMIN_DOCTOR_ROLES)
    public ResponseEntity<Void> disablePatient(@PathVariable Long id) {
        return handlePatientStatusOperation(
                "Desabilitando",
                id,
                () -> patientService.disablePatient(id)
        );
    }

    @PatchMapping("/{id}/enable")
    @PreAuthorize(ADMIN_DOCTOR_ROLES)
    public ResponseEntity<Void> enablePatient(@PathVariable Long id) {
        return handlePatientStatusOperation(
                "Habilitando",
                id,
                () -> patientService.enablePatient(id)
        );
    }
    private ResponseEntity<PatientResponseDTO> handlePatientOperation(
            String operationName,
            Long patientId,
            Supplier<Patient> operation) {

        log.info("{} paciente ID: {}", operationName, patientId);

        try {
            Patient patient = operation.get();
            PatientResponseDTO response = PatientResponseDTO.fromEntity(patient);
            return operationName.equals("Criando")
                    ? ResponseEntity.status(HttpStatus.CREATED).body(response)
                    : ResponseEntity.ok(response);
        } catch (RuntimeException exception) {
            log.error("Erro ao {}: {}", operationName.toLowerCase(), exception.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    private ResponseEntity<Void> handlePatientStatusOperation(
            String operationName,
            Long patientId,
            Runnable operation) {

        log.info("{} paciente ID: {}", operationName, patientId);

        try {
            operation.run();
            return ResponseEntity.noContent().build();
        } catch (RuntimeException exception) {
            log.error("Erro ao {}: {}", operationName.toLowerCase(), exception.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}