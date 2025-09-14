package com.healthcore.appointmentservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);

        jwtUtil.validateToken(token);

        if(token != null){
            String login = jwtUtil.getUsernameFromToken(token);
            /*Usuario usuario = usuarioRepository.findByLoginIgnoreCase(login)
                   .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

            Authentication authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);*/
        }

        filterChain.doFilter(request, response);
    }


    private String getToken(HttpServletRequest request){
        String tokenHeader = request.getHeader("Authorization");

        if(tokenHeader != null && tokenHeader.startsWith("Bearer ")){
            tokenHeader = tokenHeader.replace("Bearer ", "");
        }

        return tokenHeader;
    }

}
