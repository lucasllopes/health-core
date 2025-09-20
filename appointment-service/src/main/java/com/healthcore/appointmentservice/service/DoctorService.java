package com.healthcore.appointmentservice.service;

import com.healthcore.appointmentservice.dto.DoctorRequestDTO;
import com.healthcore.appointmentservice.dto.DoctorSearchParameters;
import com.healthcore.appointmentservice.dto.SearchParameters;
import com.healthcore.appointmentservice.dto.registration.DoctorRegistrationDTO;
import com.healthcore.appointmentservice.dto.response.DoctorResponseDTO;
import com.healthcore.appointmentservice.exception.DoctorNotFoundException;
import com.healthcore.appointmentservice.mapper.DoctorMapper;
import com.healthcore.appointmentservice.persistence.entity.Doctor;
import com.healthcore.appointmentservice.persistence.repository.DoctorRepository;
import com.healthcore.appointmentservice.service.helper.DoctorSearchParameterCleaner;
import com.healthcore.appointmentservice.service.helper.DoctorStatusManager;
import com.healthcore.appointmentservice.service.helper.DoctorUpdater;
import com.healthcore.appointmentservice.service.validator.DoctorValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    private static final Logger log = LoggerFactory.getLogger(DoctorService.class);

    private final DoctorRepository doctorRepository;
    private final UserService userService;
    private final DoctorMapper doctorMapper;
    private final DoctorValidator doctorValidator;
    private final DoctorUpdater doctorUpdater;
    private final DoctorStatusManager statusManager;
    private final DoctorSearchParameterCleaner parameterCleaner;

    public DoctorService(
            DoctorRepository doctorRepository,
            UserService userService,
            DoctorMapper doctorMapper,
            DoctorValidator doctorValidator,
            DoctorSearchParameterCleaner parameterCleaner,
            DoctorStatusManager statusManager,
            DoctorUpdater doctorUpdater
    )
    {

        this.doctorRepository = doctorRepository;
        this.userService = userService;
        this.doctorMapper = doctorMapper;
        this.doctorValidator = doctorValidator;
        this.parameterCleaner = parameterCleaner;
        this.statusManager = statusManager;
        this.doctorUpdater = doctorUpdater;
    }

    public Doctor createDoctor(DoctorRegistrationDTO registrationRequest) {

        if (registrationRequest == null) throw new IllegalArgumentException("Dados de registro não podem ser nulos");

        log.info("DoctorService | Creating doctor: {} | CRM: {}", registrationRequest.username(), registrationRequest.crm());

        return userService.saveDoctor(registrationRequest);
    }

    public Page<Doctor> getAllDoctors(Pageable pageable) {

        if (pageable == null) throw new IllegalArgumentException("Pageable não pode ser nulo");

        log.info("DoctorService | Buscando médicos com paginação: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        return doctorRepository.findAll(pageable);
    }

    public List<DoctorResponseDTO> searchDoctors(String name, String specialty, String crm) {

        log.info("DoctorService | Buscando pacientes ativos - nome: '{}', especialidade: '{}', CRM: '{}'",
                name, specialty, crm);

        DoctorSearchParameters cleanParameters = parameterCleaner.cleanSearchParameters(name, specialty, crm);

        if (!parameterCleaner.hasValidParameters(cleanParameters)) {
            log.warn("DoctorService | Nenhum parâmetro de busca válido fornecido, retornando lista vazia");
            return List.of();
        }

        List<Doctor> doctors = doctorRepository.findActiveDoctorsByFilters(
                cleanParameters.name(),
                cleanParameters.specialty(),
                cleanParameters.crm()
        );

        log.info("Encontrados {} médicos ativos para os filtros aplicados", doctors.size());

        return doctors.stream()
                .map(doctorMapper::toResponseDTO)
                .toList();
    }

    public Optional<Doctor> getDoctorById(Long doctorId) {

        log.info("DoctorService | Buscando médico por ID: {}", doctorId);

        if (doctorId == null || doctorId <= 0) {
            throw new IllegalArgumentException("DoctorService | ID do médico deve ser um número positivo");
        }

        log.info("DoctorService | Buscando médico por ID: {}", doctorId);
        return doctorRepository.findById(doctorId);
    }

    public Doctor updateDoctor(Long doctorId, DoctorRequestDTO updateRequest) {

        if (doctorId == null || doctorId <= 0) {
            throw new IllegalArgumentException("ID do médico deve ser um número positivo");
        }

        log.info("DoctorService | Atualizando médico ID: {}", doctorId);

        doctorValidator.validateUpdateRequest(updateRequest);
        Doctor doctor = findDoctorByIdOrThrow(doctorId);
        doctorUpdater.updateDoctorFields(doctor, updateRequest, doctorId);

        return doctorRepository.save(doctor);
    }

    public void disableDoctor(Long doctorId) {

        if (doctorId == null || doctorId <= 0) {
            throw new IllegalArgumentException("ID do médico deve ser um número positivo");
        }
        log.info("Desabilitando médico ID: {}", doctorId);
        changeDoctorStatus(doctorId, false);
    }

    public void enableDoctor(Long doctorId) {

        if (doctorId == null || doctorId <= 0) {
            throw new IllegalArgumentException("ID do médico deve ser um número positivo");
        }
        log.info("Desabilitando médico ID: {}", doctorId);
        changeDoctorStatus(doctorId, true);
    }

    private Doctor findDoctorByIdOrThrow(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));
    }

    private void changeDoctorStatus(Long doctorId, boolean enabled) {
        Doctor doctor = findDoctorByIdOrThrow(doctorId);
        statusManager.changeDoctorStatus(doctor, enabled);
    }
}
