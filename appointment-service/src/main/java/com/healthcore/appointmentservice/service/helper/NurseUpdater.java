package com.healthcore.appointmentservice.service.helper;

import com.healthcore.appointmentservice.dto.NurseRequestDTO;
import com.healthcore.appointmentservice.persistence.entity.Nurse;
import org.springframework.stereotype.Component;

@Component
public class NurseUpdater extends Updater{

    public void updateNurseFields(Nurse nurse, NurseRequestDTO request, Long nurseId) {
        updateIfValid(request.name(), nurse::setName);
        updateIfValid(request.coren(), nurse::setCoren);
    }
}
