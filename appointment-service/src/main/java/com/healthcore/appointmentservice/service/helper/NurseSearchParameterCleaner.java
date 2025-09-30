package com.healthcore.appointmentservice.service.helper;

import com.healthcore.appointmentservice.dto.DoctorSearchParameters;
import com.healthcore.appointmentservice.dto.NurseSearchParameters;
import org.springframework.stereotype.Component;

@Component
public class NurseSearchParameterCleaner {

    public NurseSearchParameters cleanSearchParameters(String name, String coren) {
        return new NurseSearchParameters(
                cleanParameter(name),
                cleanParameter(coren)
        );
    }

    private String cleanParameter(String parameter) {
        return (parameter != null && !parameter.trim().isEmpty()) ? parameter.trim() : null;
    }

    public boolean hasValidParameters(NurseSearchParameters parameters) {
        return parameters.name() != null ||
                parameters.coren() != null;
    }
}
