package com.healthcore.appointmentservice.service.helper;

import com.healthcore.appointmentservice.dto.SearchParameters;
import org.springframework.stereotype.Component;

@Component
public class SearchParameterCleaner {

    public SearchParameters cleanSearchParameters(String name, String email, String document) {
        return new SearchParameters(
                cleanParameter(name),
                cleanParameter(email),
                cleanParameter(document)
        );
    }

    private String cleanParameter(String parameter) {
        return (parameter != null && !parameter.trim().isEmpty()) ? parameter.trim() : null;
    }

    public boolean hasValidParameters(SearchParameters parameters) {
        return parameters.name() != null ||
                parameters.email() != null ||
                parameters.document() != null;
    }
}
