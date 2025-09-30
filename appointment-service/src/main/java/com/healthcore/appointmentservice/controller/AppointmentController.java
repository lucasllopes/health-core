package com.healthcore.appointmentservice.controller;

import com.healthcore.appointmentservice.dto.CreateAppointmentRequestDTO;
import com.healthcore.appointmentservice.dto.UpdateAppointmentRequestDTO;
import com.healthcore.appointmentservice.dto.AppointmentResponseDTO;
import com.healthcore.appointmentservice.service.AppointmentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    // Constantes de roles para controle de acesso
    private static final String ADMIN_ROLE = "hasRole('ADMIN')";
    private static final String DOCTOR_ROLE = "hasRole('DOCTOR')";
    private static final String NURSE_ROLE = "hasRole('NURSE')";
    private static final String PATIENT_ROLE = "hasRole('PATIENT')";
    private static final String ADMIN_NURSE_DOCTOR_ROLES = "hasRole('ADMIN') or hasRole('NURSE') or hasRole('DOCTOR')";
    private static final String PATIENT_SELF_ACCESS = "hasRole('PATIENT') and @appointmentService.isPatientOwner(authentication.name, #id)";

    private final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @PreAuthorize(DOCTOR_ROLE + " or " + NURSE_ROLE)
    public ResponseEntity<AppointmentResponseDTO> createAppointment(@Valid @RequestBody CreateAppointmentRequestDTO createAppointmentRequestDTO) {
        logger.info("Handling POST request to /appointments");
        AppointmentResponseDTO response = appointmentService.create(createAppointmentRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize(DOCTOR_ROLE + " or " + NURSE_ROLE + " or (" + PATIENT_ROLE + ")")
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        logger.info("Handling GET request to /appointments - page: {}, size: {}, sortBy: {}, sortDirection: {}", page, size, sortBy, sortDirection);
        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.fromString(sortDirection), sortBy));
        Page<AppointmentResponseDTO> appointmentsPage = appointmentService.getAll(pageable);
        return ResponseEntity.ok(appointmentsPage.getContent());
    }

    @GetMapping("/{id}")
    @PreAuthorize(DOCTOR_ROLE + " or " + NURSE_ROLE + " or (" + PATIENT_SELF_ACCESS + ")")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable Long id) {
        logger.info("Handling GET request to /appointments/{}", id);
        return appointmentService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize(DOCTOR_ROLE + " or " + NURSE_ROLE)
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAppointmentRequestDTO updateAppointmentRequestDTO
    ) {
        logger.info("Handling PUT request to /appointments/{}", id);
        AppointmentResponseDTO response = appointmentService.update(id, updateAppointmentRequestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(DOCTOR_ROLE + " or " + NURSE_ROLE)
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        logger.info("Handling DELETE request to /appointments/{}", id);
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
