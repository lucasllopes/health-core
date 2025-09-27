package com.healthcore.appointmentservice.controller;

import com.healthcore.appointmentservice.controller.helper.DoctorDTOConverter;
import com.healthcore.appointmentservice.controller.helper.PaginationHelper;
import com.healthcore.appointmentservice.dto.DoctorRequestDTO;
import com.healthcore.appointmentservice.dto.registration.DoctorRegistrationDTO;
import com.healthcore.appointmentservice.dto.response.DoctorResponseDTO;
import com.healthcore.appointmentservice.dto.update.DoctorUpdateDTO;
import com.healthcore.appointmentservice.persistence.entity.Doctor;
import com.healthcore.appointmentservice.service.DoctorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private static final Logger log = LoggerFactory.getLogger(DoctorController.class);

    private static final String ADMIN_ROLE = "hasRole('ADMIN')";
    private static final String ADMIN_NURSE_DOCTOR_ROLES = "hasRole('ADMIN') or hasRole('NURSE') or hasRole('DOCTOR')";
    private static final String DOCTOR_SELF_ACCESS = "hasRole('DOCTOR') and @doctorService.isDoctorOwner(authentication.name, #id)";

    private final DoctorService doctorService;
    private final PaginationHelper paginationHelper;
    private final DoctorDTOConverter dtoConverter;

    public DoctorController(DoctorService doctorService, PaginationHelper paginationHelper, DoctorDTOConverter doctorDTOConverter) {
        this.doctorService = doctorService;
        this.paginationHelper = paginationHelper;
        this.dtoConverter = doctorDTOConverter;
    }

    @PostMapping
    @PreAuthorize(ADMIN_ROLE)
    public ResponseEntity<DoctorResponseDTO> createDoctor(
            @Valid @RequestBody DoctorRegistrationDTO registrationRequest
    )
    {

        log.info("DoctorController | Handling POST request to /doctors");

        return handleDoctorOperation(
                "Criando",
                null,
                () -> doctorService.createDoctor(registrationRequest)
        );
    }

    @GetMapping
    @PreAuthorize(ADMIN_NURSE_DOCTOR_ROLES)
    public ResponseEntity<List<DoctorResponseDTO>> getAllDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    )
    {

        log.info("DoctorController | Buscando médicos - página: {}, tamanho: {}, ordenação: {} {}",
                page, size, sortBy, sortDirection);

        Pageable pageable = paginationHelper.createPageable(page, size, sortBy, sortDirection);
        Page<Doctor> doctorsPage = doctorService.getAllDoctors(pageable);

        List<DoctorResponseDTO> responseList = dtoConverter.convertPageToResponseList(doctorsPage);

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/search")
    @PreAuthorize(ADMIN_NURSE_DOCTOR_ROLES)
    public ResponseEntity<List<DoctorResponseDTO>> searchDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String crm
    )
    {

        log.info("Buscando médicos - nome '{}', especialidade '{}', CRM '{}'",
                name, specialty, crm);

        List<DoctorResponseDTO> foundDoctors = doctorService.searchDoctors(name, specialty, crm);

        log.info("Encontrados {} médicos para a busca", foundDoctors.size());

        return ResponseEntity.ok(foundDoctors);
    }

    @GetMapping("/{id}")
    @PreAuthorize(ADMIN_NURSE_DOCTOR_ROLES)
    public ResponseEntity<DoctorResponseDTO> getDoctorById(@PathVariable Long id) {

        log.info("DoctorController | Buscando médico por ID: {}", id);

        return doctorService.getDoctorById(id)
                .map(doctor -> ResponseEntity.ok(DoctorResponseDTO.fromEntity(doctor)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize(ADMIN_ROLE + " or (" + DOCTOR_SELF_ACCESS + ")")
    public ResponseEntity<DoctorResponseDTO> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorUpdateDTO updateRequest
    )
    {
        return handleDoctorOperation(
                "Atualizando",
                id,
                () -> {
                    DoctorRequestDTO requestDTO = dtoConverter.convertUpdateToRequest(id, updateRequest);
                    return doctorService.updateDoctor(id, requestDTO);
                }
        );
    }

    @PatchMapping("/{id}/disable")
    @PreAuthorize(ADMIN_ROLE)
    public ResponseEntity<Void> disableDoctor(@PathVariable Long id) {

        return handleDoctorStatusOperation(
                "Desabilitando",
                id,
                () -> doctorService.disableDoctor(id)
        );
    }

    @PatchMapping("/{id}/enable")
    @PreAuthorize(ADMIN_ROLE)
    public ResponseEntity<Void> enableDoctor(@PathVariable Long id) {

        return handleDoctorStatusOperation(
            "Habilitando",
                id,
                () -> doctorService.enableDoctor(id)
        );
    }

    private ResponseEntity<DoctorResponseDTO> handleDoctorOperation(
            String operationName,
            Long doctorId,
            Supplier<Doctor> operation
    )
    {

        log.info("DoctorController.handleDoctorOperation() | {} doctor ID: {}", operationName, doctorId);

        try {

            Doctor doctor = operation.get();
            DoctorResponseDTO response = DoctorResponseDTO.fromEntity(doctor);
            return operationName.equals("Criando")
                    ? ResponseEntity.status(HttpStatus.CREATED).body(response)
                    : ResponseEntity.ok(response);
        } catch (RuntimeException exception) {

            log.error("DoctorController | Erro ao {}: {}", operationName.toLowerCase(), exception.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    private ResponseEntity<Void> handleDoctorStatusOperation(
            String operationName,
            Long doctorId,
            Runnable operation
    )
    {

        log.info("DoctorController.handleDoctorStatusOperation() | {} doctor ID: {}", operationName, doctorId);

        try {
            operation.run();
            return ResponseEntity.noContent().build();
        } catch (RuntimeException exception) {
            log.error("DoctorController.handleDoctorStatusOperation() | Erro ao {}: {}", operationName.toLowerCase(), exception.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
