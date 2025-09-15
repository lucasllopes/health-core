package com.healthcore.appointmentservice.service;

import com.healthcore.appointmentservice.persistence.entity.Appointment;
import com.healthcore.appointmentservice.persistence.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentGraphqlService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentGraphqlService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public Appointment findById(Long id){
        return appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    public List<Appointment> findAll(){
        return appointmentRepository.findAll();
    }
}
