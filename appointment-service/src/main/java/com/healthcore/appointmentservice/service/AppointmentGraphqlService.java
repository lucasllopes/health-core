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

    public List<Appointment> findByNameIgnoreCase(String name){
        return appointmentRepository.findByNameIgnoreCase(name);
    }

    public List<Appointment> findAll(){
        return appointmentRepository.findAll();
    }
}
