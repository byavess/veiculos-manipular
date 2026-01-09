package org.veiculo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.veiculo.model.dto.ModeloResponse;
import org.veiculo.model.entity.Modelo;
import org.veiculo.model.repository.ModeloRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModeloService {

    private final ModeloRepository modeloRepository;

    @Transactional(readOnly = true)
    public List<ModeloResponse> listarTodos() {
        return modeloRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ModeloResponse> listarPorMarcaId(Long marcaId) {
        return modeloRepository.findByMarcaId(marcaId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ModeloResponse buscarPorId(Long id) {
        Modelo modelo = modeloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modelo não encontrado com ID: " + id));
        return toResponse(modelo);
    }

    private ModeloResponse toResponse(Modelo modelo) {
        return ModeloResponse.builder()
                .id(modelo.getId())
                .modelo(modelo.getModelo())
                .marcaId(modelo.getMarca() != null ? modelo.getMarca().getId() : null)
                .marcaNome(modelo.getMarca() != null ? modelo.getMarca().getNome() : null)
                .build();
    }
}

