package com.healthcore.appointmentservice.service.helper;

import com.healthcore.appointmentservice.persistence.entity.Patient;
import com.healthcore.appointmentservice.persistence.entity.User;
import com.healthcore.appointmentservice.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class PatientStatusManager {

    private final UserRepository userRepository;

    public PatientStatusManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void changePatientStatus(Patient patient, boolean enabled) {
        User user = patient.getUser();
        if (user != null) {
            user.setEnabled(enabled);
            userRepository.save(user);
        }
    }
}
