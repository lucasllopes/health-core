package com.healthcore.appointmentservice.controller;

import com.healthcore.appointmentservice.dto.AuthResponseDTO;
import com.healthcore.appointmentservice.dto.LoginRequestDTO;
import com.healthcore.appointmentservice.security.JwtUtil;
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

//    private final UsuarioService usuarioService;
//
//    private final JwtUtil jwtUtil;
//
//    public AuthController(UsuarioService usuarioService, JwtUtil jwtUtil) {
//        this.usuarioService = usuarioService;
//        this.jwtUtil = jwtUtil;
//    }
//
//    @PostMapping("login")
//    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO dto) {
//        log.info("Handling POST request to /usuarios");
//
//        usuarioService.validateCredentials(dto.login(), dto.password());
//
//        String acessToken = jwtUtil.generateToken(dto.login());
//        String refreshToken = jwtUtil.generateRefreshToken(dto.login());
//
//        return ResponseEntity.ok(new AuthResponseDTO(acessToken, refreshToken));
//    }


}
