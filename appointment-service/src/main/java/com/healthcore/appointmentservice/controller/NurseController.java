package com.healthcore.appointmentservice.controller;

import com.healthcore.appointmentservice.controller.helper.NurseDTOConverter;
import com.healthcore.appointmentservice.controller.helper.PaginationHelper;
import com.healthcore.appointmentservice.dto.registration.NurseRegistrationDTO;
import com.healthcore.appointmentservice.dto.response.NurseResponseDTO;
import com.healthcore.appointmentservice.dto.update.NurseUpdateDTO;
import com.healthcore.appointmentservice.persistence.entity.Nurse;
import com.healthcore.appointmentservice.service.NurseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nurses")
public class NurseController {

    private static final Logger log = LoggerFactory.getLogger(NurseController.class);


    private static final String ADMIN_ROLE = "hasRole('ADMIN')";
    private static final String ADMIN_NURSE_DOCTOR_ROLES = "hasRole('ADMIN') or hasRole('NURSE') or hasRole('DOCTOR')";
    private static final String NURSE_SELF_ACCESS = "hasRole('NURSE') and @nurseService.isNurseOwner(authentication.name, #id)";
    private final NurseService service;
    private final PaginationHelper paginationHelper;
    private final NurseDTOConverter converter;

    public NurseController(NurseService service, PaginationHelper paginationHelper, NurseDTOConverter converter) {
        this.service = service;
        this.paginationHelper = paginationHelper;
        this.converter = converter;
    }


    @GetMapping
    @PreAuthorize(ADMIN_NURSE_DOCTOR_ROLES)
    public ResponseEntity<List<NurseResponseDTO>> getAllNurses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    )
    {

        log.info("NurseController | Buscando enfermeiros - página: {}, tamanho: {}, ordenação: {} {}",
                page, size, sortBy, sortDirection);

        Pageable pageable = paginationHelper.createPageable(page, size, sortBy, sortDirection);
        Page<Nurse> nursesPage = service.getAllNurses(pageable);

        List<NurseResponseDTO> responseList = converter.convertPageToResponseList(nursesPage);

        return ResponseEntity.ok(responseList);
    }


    @PostMapping
    @PreAuthorize(ADMIN_ROLE)
    public ResponseEntity<NurseResponseDTO> createNurse(@RequestBody NurseRegistrationDTO request) {
        Nurse nurse = service.createNurse(request);
        return ResponseEntity.ok(NurseResponseDTO.fromEntity(nurse));
    }

    @GetMapping("/{id}")
    @PreAuthorize(ADMIN_NURSE_DOCTOR_ROLES + " or (" + NURSE_SELF_ACCESS + ")")
    public ResponseEntity<NurseResponseDTO> getNurseById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(NurseResponseDTO.fromEntity(service.getNurseById(id)));
    }

    @GetMapping("/search")
    @PreAuthorize(ADMIN_NURSE_DOCTOR_ROLES)
    public ResponseEntity<List<NurseResponseDTO>> searchNurses(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String coren
    )
    {

        log.info("Buscando enfermeiros - nome '{}', coren '{}'",
                name, coren);

        List<NurseResponseDTO> foundNurses = service.searchNurses(name, coren);

        log.info("Encontrados {} enfermeiros para a busca", foundNurses.size());

        return ResponseEntity.ok(foundNurses);
    }

    @PutMapping("/{id}")
    @PreAuthorize(ADMIN_ROLE + " or (" + NURSE_SELF_ACCESS + ")")
    public ResponseEntity<NurseResponseDTO> updateNurse(@PathVariable("id") Long id,
                                             @RequestBody NurseUpdateDTO request) {
        Nurse updatedNurse = service.updateNurse(id, request);
        return ResponseEntity.ok(NurseResponseDTO.fromEntity(updatedNurse));
    }

    @PatchMapping("/{id}/disable")
    @PreAuthorize(ADMIN_ROLE)
    public ResponseEntity<Void> disableNurse(@PathVariable Long id) {
        service.disableNurse(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/enable")
    @PreAuthorize(ADMIN_ROLE)
    public ResponseEntity<Void> enableDoctor(@PathVariable Long id) {
        service.enableNurse(id);
        return ResponseEntity.noContent().build();
    }

}
