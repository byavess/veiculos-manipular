package org.veiculo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.veiculo.model.repository.TokenRepository;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public void revokeAllTokens() {
        tokenRepository.findAll().forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
            tokenRepository.save(token);
        });
    }
}

