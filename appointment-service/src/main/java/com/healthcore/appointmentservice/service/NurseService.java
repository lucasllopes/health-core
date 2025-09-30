package com.healthcore.appointmentservice.service;

import com.healthcore.appointmentservice.dto.NurseSearchParameters;
import com.healthcore.appointmentservice.dto.registration.NurseRegistrationDTO;
import com.healthcore.appointmentservice.dto.response.NurseResponseDTO;
import com.healthcore.appointmentservice.dto.update.NurseUpdateDTO;
import com.healthcore.appointmentservice.exception.NurseNotFoundException;
import com.healthcore.appointmentservice.mapper.NurseMapper;
import com.healthcore.appointmentservice.persistence.entity.Nurse;
import com.healthcore.appointmentservice.persistence.repository.NurseRepository;
import com.healthcore.appointmentservice.service.helper.NurseSearchParameterCleaner;
import com.healthcore.appointmentservice.service.helper.NurseStatusManager;
import com.healthcore.appointmentservice.service.helper.NurseUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NurseService {

    private static final Logger log = LoggerFactory.getLogger(NurseService.class);

    private final NurseRepository repository;
    private final UserService userService;
    private final NurseUpdater updater;
    private final NurseStatusManager nurseStatusManager;
    private final NurseSearchParameterCleaner parameterCleaner;
    private final NurseMapper mapper;

    public NurseService(NurseRepository repository, UserService userService, NurseUpdater updater, NurseStatusManager nurseStatusManager, NurseSearchParameterCleaner parameterCleaner, NurseMapper mapper) {
        this.repository = repository;
        this.userService = userService;
        this.updater = updater;
        this.nurseStatusManager = nurseStatusManager;
        this.parameterCleaner = parameterCleaner;
        this.mapper = mapper;
    }

    @Transactional
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
        log.info("Buscando enfermeiros com paginação: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Nurse getNurseById(Long nurseId) {
        if (nurseId == null || nurseId <= 0) {
            throw new IllegalArgumentException("ID do enfermeiro deve ser um número positivo");
        }
        log.info("Buscando enfermeiro por ID: {}", nurseId);
        return repository.findById(nurseId).orElseThrow(() -> new NurseNotFoundException(nurseId));
    }

    @Transactional
    public Nurse updateNurse(Long nurseId, NurseUpdateDTO request) {
        if (nurseId == null || nurseId <= 0) {
            throw new IllegalArgumentException("ID do enfermeiro deve ser um número positivo");
        }

        if (request == null) {
            throw new IllegalArgumentException("Request não pode ser nulo");
        }

        log.info("Atualizando enfermeiro ID: {}", nurseId);

        Nurse nurse = repository.findById(nurseId).orElseThrow(() -> new NurseNotFoundException(nurseId));
        updater.updateNurseFields(nurse, request);
        return repository.save(nurse);
    }

    public List<NurseResponseDTO> searchNurses(String name, String coren) {
        log.info("Buscando enfermeiros ativos - nome: '{}', coren: '{}'", name, coren);

        NurseSearchParameters cleanParameters = parameterCleaner.cleanSearchParameters(name, coren);

        if (!parameterCleaner.hasValidParameters(cleanParameters)) {
            log.warn("Nenhum parâmetro de busca válido fornecido, retornando lista vazia");
            return List.of();
        }

        List<Nurse> nurses = repository.findActiveNursesByFilters(
                cleanParameters.name(),
                cleanParameters.coren()
        );

        log.info("Encontrados {} enfermeiros ativos para os filtros aplicados", nurses.size());

        return nurses.stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    public void disableNurse(Long nurseId) {

        if (nurseId == null || nurseId <= 0) {
            throw new IllegalArgumentException("ID do enfermeiro deve ser um número positivo");
        }
        log.info("Desabilitando enfermeiro ID: {}", nurseId);
        changeNurseStatus(nurseId, false);
    }

    public void enableNurse(Long nurseId) {

        if (nurseId == null || nurseId <= 0) {
            throw new IllegalArgumentException("ID do enfermeiro deve ser um número positivo");
        }
        log.info("Desabilitando enfermeiro ID: {}", nurseId);
        changeNurseStatus(nurseId, true);
    }

    private Nurse findNurseByIdOrThrow(Long nurseId){
        return repository.findById(nurseId).orElseThrow(() -> new NurseNotFoundException(nurseId));
    }

    private void changeNurseStatus(Long nurseId, boolean enabled) {
        Nurse nurse = findNurseByIdOrThrow(nurseId);
        nurseStatusManager.changeNurseStatus(nurse, enabled);
    }
}
