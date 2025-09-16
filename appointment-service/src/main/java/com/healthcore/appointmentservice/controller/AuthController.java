package com.healthcore.appointmentservice.controller;

import com.healthcore.appointmentservice.dto.AuthResponseDTO;
import com.healthcore.appointmentservice.dto.LoginRequestDTO;
import com.healthcore.appointmentservice.dto.RefreshTokenRequestDTO;
import com.healthcore.appointmentservice.persistence.entity.User;
import com.healthcore.appointmentservice.security.JwtUtil;
import com.healthcore.appointmentservice.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        log.info("Handling POST request to /auth/login");

        userService.validateCredentials(dto.login(), dto.password());

        String accessToken = jwtUtil.generateToken(dto.login());
        String refreshToken = jwtUtil.generateRefreshToken(dto.login());

        return ResponseEntity.ok(new AuthResponseDTO(accessToken, refreshToken));
    }

    @PostMapping("refresh-token")
    public ResponseEntity<AuthResponseDTO> atualizarToken(@Valid @RequestBody RefreshTokenRequestDTO dto) {
        String username = jwtUtil.getUsernameFromRefreshToken(dto.refreshToken());
        User user = userService.loadUserByUsername(username);
        String newToken = jwtUtil.refreshAccessToken(dto.refreshToken(), user.getUsername());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());
        return ResponseEntity.ok(new AuthResponseDTO(newToken, newRefreshToken));
    }


}
