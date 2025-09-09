package com.healthcore.medicalcareservice.service;

import com.healthcore.medicalcareservice.persistence.entity.Appointment;
import com.healthcore.medicalcareservice.persistence.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public List<Appointment> findByNameIgnoreCase(String name){
        return appointmentRepository.findByNameIgnoreCase(name);
    }

    public List<Appointment> findAll(){
        return appointmentRepository.findAll();
    }
}
