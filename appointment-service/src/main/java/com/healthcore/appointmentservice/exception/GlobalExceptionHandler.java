package com.healthcore.appointmentservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.authorization.AuthorizationDeniedException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Erro de runtime: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                "RUNTIME_ERROR",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        log.error("Erro de autenticação: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                "AUTHENTICATION_ERROR",
                "Credenciais inválidas ou usuário não encontrado",
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        log.error("Credenciais inválidas: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                "BAD_CREDENTIALS",
                "Username ou senha incorretos",
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Erro de validação: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePatientNotFoundException(PatientNotFoundException ex) {
        log.error("Paciente não encontrado: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "PATIENT_NOT_FOUND",
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MedicalRecordNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMedicalRecordNotFoundException(MedicalRecordNotFoundException ex) {
        log.error("MedicalRecord não encontrado: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                "MEDICAL_RECORD_NOT_FOUND",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MedicalRecordValidationException.class)
    public ResponseEntity<ErrorResponse> handleMedicalRecordValidationException(MedicalRecordValidationException ex) {
        log.error("Erro de validação de MedicalRecord: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                "MEDICAL_RECORD_VALIDATION_ERROR",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Erro interno do servidor: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "Erro interno do servidor. Contate o administrador.",
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.error("Acesso negado: {}", ex.getMessage());

        String customMessage = determineAuthorizationMessage(ex);

        ErrorResponse error = new ErrorResponse(
                "ACCESS_DENIED",
                customMessage,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    private String determineAuthorizationMessage(AuthorizationDeniedException ex) {
        String message = ex.getMessage();

        if (message != null && message.contains("PATIENT")) {
            return "Acesso negado: Pacientes só podem visualizar seus próprios dados";
        } else if (message != null && message.contains("hasRole")) {
            return "Acesso negado: Você não possui permissão para acessar este recurso";
        }

        return "Acesso negado: Permissões insuficientes para esta operação";
    }

    public record ErrorResponse(
            String code,
            String message,
            LocalDateTime timestamp
    ) {}
}