package com.healthcore.appointmentservice.controller;

import com.healthcore.appointmentservice.dto.AuthResponseDTO;
import com.healthcore.appointmentservice.dto.LoginRequestDTO;
import com.healthcore.appointmentservice.security.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        log.info("Handling POST request to /usuarios");

        //usuarioService.validateCredentials(dto.login(), dto.password());

        String acessToken = jwtUtil.generateToken(dto.login());
        String refreshToken = jwtUtil.generateRefreshToken(dto.login());

        return ResponseEntity.ok(new AuthResponseDTO(acessToken, refreshToken));
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
