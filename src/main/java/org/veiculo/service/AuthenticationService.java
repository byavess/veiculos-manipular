package org.veiculo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.veiculo.model.dto.AuthResponse;
import org.veiculo.model.dto.LoginRequest;
import org.veiculo.model.entity.Token;
import org.veiculo.model.repository.TokenRepository;
import org.veiculo.model.repository.UserRepository;
import org.veiculo.security.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;


    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            log.error("[DEBUG] Falha na autenticação: {} - {}", e.getClass(), e.getMessage());
            throw e;
        }
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + request.getUsername()));
        var jwtToken = jwtUtil.generateToken(user);
        // Salva o token emitido no banco
        Token token = Token.builder()
                .token(jwtToken)
                .user(user)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
        Long expiration = jwtUtil.extractExpirationInMinutes(jwtToken);
        return AuthResponse.builder()
                .token(jwtToken)
                .type("Bearer")
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .nomeCompleto(user.getNomeCompleto())
                .expiration(expiration)
                .build();
    }

    public Map<String, Object> verificarAutenticacao() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        boolean valid = false;
        String username = null;
        Object authorities = null;

        if (auth != null
                && !(auth instanceof AnonymousAuthenticationToken)
                && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal())) {
            username = auth.getName();
            // Busca o usuário no banco para garantir que está ativo
            var userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent() && userOpt.get().isEnabled()) {
                valid = true;
                authorities = auth.getAuthorities();
            }
        }

        response.put("valid", valid);
        response.put("username", valid ? username : null);
        response.put("authorities", valid ? authorities : null);
        return response;
    }
}
