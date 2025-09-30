package com.healthcore.appointmentservice.service.helper;

import com.healthcore.appointmentservice.persistence.entity.Doctor;
import com.healthcore.appointmentservice.persistence.entity.Nurse;
import com.healthcore.appointmentservice.persistence.entity.User;
import com.healthcore.appointmentservice.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class NurseStatusManager {

    private final UserRepository userRepository;

    public NurseStatusManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void changeNurseStatus(Nurse nurse, boolean enabled) {
        User user = nurse.getUser();
        if (user != null) {
            user.setEnabled(enabled);
            userRepository.save(user);
        }
    }
}
