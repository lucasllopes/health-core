package com.healthcore.appointmentservice.dto.graphql;

public record AppointmentFilterInput(
        String patientDocument,
        String doctorCrm,
        Boolean futureOnly
) {}
