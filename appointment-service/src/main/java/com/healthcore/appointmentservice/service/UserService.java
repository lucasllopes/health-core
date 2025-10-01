package com.healthcore.appointmentservice.service;

import com.healthcore.appointmentservice.dto.registration.DoctorRegistrationDTO;
import com.healthcore.appointmentservice.dto.registration.NurseRegistrationDTO;
import com.healthcore.appointmentservice.dto.registration.PatientRegistrationDTO;
import com.healthcore.appointmentservice.enums.UserRole;
import com.healthcore.appointmentservice.exception.DocumentAlreadyExistsException;
import com.healthcore.appointmentservice.persistence.entity.Doctor;
import com.healthcore.appointmentservice.persistence.entity.Nurse;
import com.healthcore.appointmentservice.persistence.entity.Patient;
import com.healthcore.appointmentservice.persistence.entity.User;
import com.healthcore.appointmentservice.persistence.repository.DoctorRepository;
import com.healthcore.appointmentservice.persistence.repository.NurseRepository;
import com.healthcore.appointmentservice.persistence.repository.PatientRepository;
import com.healthcore.appointmentservice.persistence.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final NurseRepository nurseRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       DoctorRepository doctorRepository,
                       NurseRepository nurseRepository,
                       PatientRepository patientRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.nurseRepository = nurseRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Carregando usuário: {}", username);
        return userRepository.findByusernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    public Doctor saveDoctor(DoctorRegistrationDTO dto) {
        log.info("Salvando médico: {}", dto.username());

        validateUsernameNotExists(dto.username());

        User user = createUser(dto.username(), dto.password(), UserRole.DOCTOR);
        User savedUser = userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setUser(savedUser);
        doctor.setName(dto.name());
        doctor.setSpecialty(dto.specialty());
        doctor.setCrm(dto.crm());

        return doctorRepository.save(doctor);
    }

    public Nurse saveNurse(NurseRegistrationDTO dto) {
        log.info("Salvando enfermeiro: {}", dto.username());

        validateUsernameNotExists(dto.username());

        User user = createUser(dto.username(), dto.password(), UserRole.NURSE);
        User savedUser = userRepository.save(user);

        Nurse nurse = new Nurse();
        nurse.setUser(savedUser);
        nurse.setName(dto.name());
        nurse.setCoren(dto.coren());

        return nurseRepository.save(nurse);
    }

    public Patient savePatient(PatientRegistrationDTO dto) {
        log.info("Salvando paciente: {}", dto.username());

        validateUsernameNotExists(dto.username());
        validateDocumentNotExists(dto.document());
        User user = createUser(dto.username(), dto.password(), UserRole.PATIENT);
        User savedUser = userRepository.save(user);

        Patient patient = new Patient();
        patient.setUser(savedUser);
        patient.setName(dto.name());
        patient.setDateOfBirth(dto.dateOfBirth());
        patient.setDocument(dto.document());
        patient.setPhone(dto.phone());
        patient.setEmail(dto.email());
        patient.setAddress(dto.address());

        return patientRepository.save(patient);
    }

    public User saveUser(String username, String password, UserRole role) {
        log.info("Salvando usuário: {} com role: {}", username, role);

        validateUsernameNotExists(username);

        User user = createUser(username, password, role);
        return userRepository.save(user);
    }

    public void validateCredentials(String username, String password) {
        log.info("Validando credenciais para: {}", username);

        User user = userRepository.findByusernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("Usuário desabilitado");
        }
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByusernameIgnoreCase(username);
    }

    private User createUser(String username, String password, UserRole role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role.getRole());
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    private void validateUsernameNotExists(String username) {
        if (userRepository.findByusernameIgnoreCase(username).isPresent()) {
            throw new RuntimeException("Username já existe: " + username);
        }
    }
    private void validateDocumentNotExists(String document) {
        if (patientRepository.findByDocument(document).isPresent()) {
            throw new DocumentAlreadyExistsException("Já existe um usuário com este documento vinculado: " + document);
        }
    }
}