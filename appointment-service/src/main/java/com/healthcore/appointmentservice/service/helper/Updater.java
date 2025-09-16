package com.healthcore.appointmentservice.service.helper;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public abstract class Updater {

    protected <T> void updateIfPresent(T value, java.util.function.Consumer<T> setter) {
        Optional.ofNullable(value).ifPresent(setter);
    }

    protected void updateIfValid(String value, java.util.function.Consumer<String> setter) {
        if (isValidString(value)) setter.accept(value);
    }

    private boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
