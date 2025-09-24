package com.healthcore.appointmentservice.dto.graphql;

import com.healthcore.appointmentservice.pagination.PageOutput;
import com.healthcore.appointmentservice.persistence.entity.Appointment;

import java.util.List;

public record AppointmentPageGraphql(List<Appointment> content, PageOutput pageInfo) {
}
