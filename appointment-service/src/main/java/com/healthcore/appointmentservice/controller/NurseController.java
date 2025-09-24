package com.healthcore.appointmentservice.controller;

import com.healthcore.appointmentservice.controller.helper.DoctorDTOConverter;
import com.healthcore.appointmentservice.controller.helper.NurseDTOConverter;
import com.healthcore.appointmentservice.controller.helper.PaginationHelper;
import com.healthcore.appointmentservice.dto.NurseRequestDTO;
import com.healthcore.appointmentservice.dto.registration.NurseRegistrationDTO;
import com.healthcore.appointmentservice.dto.response.DoctorResponseDTO;
import com.healthcore.appointmentservice.dto.response.NurseResponseDTO;
import com.healthcore.appointmentservice.persistence.entity.Doctor;
import com.healthcore.appointmentservice.persistence.entity.Nurse;
import com.healthcore.appointmentservice.service.NurseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nurses")
public class NurseController {

    private static final Logger log = LoggerFactory.getLogger(NurseController.class);


    private static final String ADMIN_ROLE = "hasRole('ADMIN')";
    private final NurseService service;
    private final PaginationHelper paginationHelper;
    private final NurseDTOConverter converter;

    public NurseController(NurseService service, PaginationHelper paginationHelper, NurseDTOConverter converter) {
        this.service = service;
        this.paginationHelper = paginationHelper;
        this.converter = converter;
    }


    @GetMapping
    // @PreAuthorize(ADMIN_ROLE) -- TODO: QUAL VAI SER O NIVEL DE ACESSO
    public ResponseEntity<List<NurseResponseDTO>> getAllNurses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    )
    {

        log.info("NurseController | Buscando médicos - página: {}, tamanho: {}, ordenação: {} {}",
                page, size, sortBy, sortDirection);

        Pageable pageable = paginationHelper.createPageable(page, size, sortBy, sortDirection);
        Page<Nurse> nursesPage = service.getAllNurses(pageable);

        List<NurseResponseDTO> responseList = converter.convertPageToResponseList(nursesPage);

        return ResponseEntity.ok(responseList);
    }


    @PostMapping
    @PreAuthorize(ADMIN_ROLE)
    public ResponseEntity<Nurse> createNurse(@RequestBody NurseRegistrationDTO request) {
        Nurse nurse = service.createNurse(request);
        return new ResponseEntity<>(nurse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Nurse> getNurseById(@PathVariable("id") Long id) {
        return service.getNurseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Atualizar enfermeiro
    @PutMapping("/{id}")
    public ResponseEntity<Nurse> updateNurse(@PathVariable("id") Long id,
                                             @RequestBody NurseRequestDTO request) {
        Nurse updatedNurse = service.updateNurse(id, request);
        return ResponseEntity.ok(updatedNurse);
    }
}
