package com.healthcore.appointmentservice.service;

import com.healthcore.appointmentservice.dto.SearchParameters;
import com.healthcore.appointmentservice.dto.registration.PatientRegistrationDTO;
import com.healthcore.appointmentservice.dto.PatientRequestDTO;
import com.healthcore.appointmentservice.dto.response.PatientResponseDTO;
import com.healthcore.appointmentservice.exception.PatientNotFoundException;
import com.healthcore.appointmentservice.mapper.PatientMapper;
import com.healthcore.appointmentservice.persistence.entity.Patient;
import com.healthcore.appointmentservice.persistence.repository.PatientRepository;
import com.healthcore.appointmentservice.service.helper.PatientStatusManager;
import com.healthcore.appointmentservice.service.helper.PatientUpdater;
import com.healthcore.appointmentservice.service.helper.SearchParameterCleaner;
import com.healthcore.appointmentservice.service.validator.PatientValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PatientService {

    private static final Logger log = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository patientRepository;
    private final UserService userService;
    private final PatientMapper patientMapper;
    private final PatientValidator patientValidator;
    private final SearchParameterCleaner parameterCleaner;
    private final PatientStatusManager statusManager;
    private final PatientUpdater patientUpdater;

    public PatientService(PatientRepository patientRepository,
                          UserService userService,
                          PatientMapper patientMapper,
                          PatientValidator patientValidator,
                          SearchParameterCleaner parameterCleaner,
                          PatientStatusManager statusManager,
                          PatientUpdater patientUpdater) {
        this.patientRepository = patientRepository;
        this.userService = userService;
        this.patientMapper = patientMapper;
        this.patientValidator = patientValidator;
        this.parameterCleaner = parameterCleaner;
        this.statusManager = statusManager;
        this.patientUpdater = patientUpdater;
    }

    public Patient createPatient(PatientRegistrationDTO registrationRequest) {
        if (registrationRequest == null) {
            throw new IllegalArgumentException("Dados de registro não podem ser nulos");
        }
        log.info("Criando paciente: {}", registrationRequest.username());
        return userService.savePatient(registrationRequest);
    }

    public Page<Patient> getAllPatients(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable não pode ser nulo");
        }
        log.info("Buscando pacientes com paginação: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return patientRepository.findAll(pageable);
    }

    public Optional<Patient> getPatientById(Long patientId) {
        if (patientId == null || patientId <= 0) {
            throw new IllegalArgumentException("ID do paciente deve ser um número positivo");
        }
        log.info("Buscando paciente por ID: {}", patientId);
        return patientRepository.findById(patientId);
    }
    public Optional<Patient> getPatientByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username não pode ser nulo ou vazio");
        }

        return patientRepository.findByUser_UsernameIgnoreCase(username);
    }


    public Optional<Patient> getPatientByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("ID do usuário deve ser um número positivo");
        }
        log.info("Buscando paciente por user ID: {}", userId);
        return patientRepository.findByUserId(userId);
    }

    public Patient updatePatient(Long patientId, PatientRequestDTO updateRequest) {
        if (patientId == null || patientId <= 0) {
            throw new IllegalArgumentException("ID do paciente deve ser um número positivo");
        }

        log.info("Atualizando paciente ID: {}", patientId);

        patientValidator.validateUpdateRequest(updateRequest);
        Patient patient = findPatientByIdOrThrow(patientId);
        patientUpdater.updatePatientFields(patient, updateRequest, patientId);

        return patientRepository.save(patient);
    }

    public void disablePatient(Long patientId) {
        if (patientId == null || patientId <= 0) {
            throw new IllegalArgumentException("ID do paciente deve ser um número positivo");
        }
        log.info("Desabilitando paciente ID: {}", patientId);
        changePatientStatus(patientId, false);
    }

    public void enablePatient(Long patientId) {
        if (patientId == null || patientId <= 0) {
            throw new IllegalArgumentException("ID do paciente deve ser um número positivo");
        }
        log.info("Habilitando paciente ID: {}", patientId);
        changePatientStatus(patientId, true);
    }

    public List<PatientResponseDTO> searchPatients(String name, String email, String document) {
        log.info("Buscando pacientes ativos - nome: '{}', email: '{}', documento: '{}'",
                name, email, document);

        SearchParameters cleanParameters = parameterCleaner.cleanSearchParameters(name, email, document);

        if (!parameterCleaner.hasValidParameters(cleanParameters)) {
            log.warn("Nenhum parâmetro de busca válido fornecido, retornando lista vazia");
            return List.of();
        }

        List<Patient> patients = patientRepository.findActivePatientsByFilters(
                cleanParameters.name(),
                cleanParameters.email(),
                cleanParameters.document()
        );

        log.info("Encontrados {} pacientes ativos para os filtros aplicados", patients.size());

        return patients.stream()
                .map(patientMapper::toResponseDTO)
                .toList();
    }
    public boolean isPatientOwner(String username, Long patientId) {
        if (username == null || patientId == null) {
            return false;
        }

        Optional<Patient> patient = getPatientByUsername(username);
        return patient.isPresent() && patient.get().getId().equals(patientId);
    }

    private Patient findPatientByIdOrThrow(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(patientId));
    }

    private void changePatientStatus(Long patientId, boolean enabled) {
        Patient patient = findPatientByIdOrThrow(patientId);
        statusManager.changePatientStatus(patient, enabled);
    }
}
