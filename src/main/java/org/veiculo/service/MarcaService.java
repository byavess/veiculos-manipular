package org.veiculo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.veiculo.model.dto.MarcaResponse;
import org.veiculo.model.entity.Marca;
import org.veiculo.model.repository.MarcaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class MarcaService {

    private final MarcaRepository marcaRepository;


    public List<MarcaResponse> listarTodas() {
        return marcaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    private MarcaResponse toResponse(Marca marca) {
        return MarcaResponse.builder()
                .id(marca.getId())
                .nome(marca.getNome())
                .build();
    }
}

