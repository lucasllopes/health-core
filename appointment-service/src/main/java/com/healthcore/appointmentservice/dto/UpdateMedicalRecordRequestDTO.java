package com.healthcore.appointmentservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateMedicalRecordRequestDTO {
    @NotNull
    @Size(min = 1, max = 2000)
    private String diagnosis;

    @Size(max = 2000)
    private String prescription;

    @Size(max = 2000)
    private String observations;

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}

