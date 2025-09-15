package com.healthcore.appointmentservice.controller;

import com.healthcore.appointmentservice.dto.AuthResponseDTO;
import com.healthcore.appointmentservice.dto.LoginRequestDTO;
import com.healthcore.appointmentservice.security.JwtUtil;
import com.healthcore.appointmentservice.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<AuthResponseDTO> atualizarToken(@Valid String refreshToken) {
        String newToken = "";
        String newRefreshToken = "";
        if (jwtUtil.validateToken(refreshToken)) {
            //Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(() -> new NegocioException("Usuário não encontrado."));
            newToken = jwtUtil.refreshAccessToken(refreshToken);
            //newRefreshToken = jwtUtil.generateRefreshToken(usuario);
        }
        return ResponseEntity.ok(new AuthResponseDTO(newToken, newRefreshToken));
    }


}
