package com.healthcore.appointmentservice.service;

import com.healthcore.appointmentservice.dto.NurseRequestDTO;
import com.healthcore.appointmentservice.dto.registration.NurseRegistrationDTO;
import com.healthcore.appointmentservice.exception.NurseNotFoundException;
import com.healthcore.appointmentservice.persistence.entity.Nurse;
import com.healthcore.appointmentservice.persistence.repository.NurseRepository;
import com.healthcore.appointmentservice.service.helper.NurseUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class NurseService {

    private static final Logger log = LoggerFactory.getLogger(NurseService.class);

    private final NurseRepository repository;
    private final UserService userService;
    private final NurseUpdater updater;

    public NurseService(NurseRepository repository, UserService userService, NurseUpdater updater) {
        this.repository = repository;
        this.userService = userService;
        this.updater = updater;
    }

    public Nurse createNurse(NurseRegistrationDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados de registro não podem ser nulos");
        }
        log.info("Criando enfermeiro: {}", request.username());
        return userService.saveNurse(request);
    }

    @Transactional(readOnly = true)
    public Page<Nurse> getAllNurses(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable não pode ser nulo");
        }
        log.info("Buscando pacientes com paginação: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Nurse> getNurseById(Long nurseId) {
        if (nurseId == null || nurseId <= 0) {
            throw new IllegalArgumentException("ID do paciente deve ser um número positivo");
        }
        log.info("Buscando paciente por ID: {}", nurseId);
        return repository.findById(nurseId);
    }


    @Transactional(readOnly = true)
    public Optional<Nurse> getNurseByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("ID do usuário deve ser um número positivo");
        }
        log.info("Buscando paciente por user ID: {}", userId);
        return repository.findByUserId(userId);
    }

    //TODO: verificar BUGGGGGG
    @Transactional
    public Nurse updateNurse(Long nurseId, NurseRequestDTO request) {
        if (nurseId == null || nurseId <= 0) {
            throw new IllegalArgumentException("ID do paciente deve ser um número positivo");
        }

        if (request == null) {
            throw new IllegalArgumentException("Request não pode ser nulo");
        }

        log.info("Atualizando paciente ID: {}", nurseId);

        Nurse nurse = repository.findByUserId(nurseId).orElseThrow(() -> new NurseNotFoundException(nurseId));
        updater.updateNurseFields(nurse, request, nurseId);

        return repository.save(nurse);
    }

}
