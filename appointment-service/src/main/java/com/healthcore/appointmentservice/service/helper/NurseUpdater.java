package com.healthcore.appointmentservice.service.helper;

import com.healthcore.appointmentservice.dto.update.NurseUpdateDTO;
import com.healthcore.appointmentservice.persistence.entity.Nurse;
import org.springframework.stereotype.Component;

@Component
public class NurseUpdater extends Updater{

    public void updateNurseFields(Nurse nurse, NurseUpdateDTO request) {
        updateIfValid(request.name(), nurse::setName);
        updateIfValid(request.coren(), nurse::setCoren);
    }
}
