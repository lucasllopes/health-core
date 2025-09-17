package com.healthcore.appointmentservice.pagination;

public record PageOutput(int page, int size, int totalPages, Long totalElements) {

}
