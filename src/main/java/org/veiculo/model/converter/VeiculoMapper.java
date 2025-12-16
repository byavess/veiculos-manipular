package org.veiculo.model.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.veiculo.model.dto.VeiculoRequest;
import org.veiculo.model.entity.Veiculo;

public class VeiculoMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Veiculo mapperVeiculoRequestParaVeiculo(VeiculoRequest veiculoRequest) {
        return objectMapper.convertValue(veiculoRequest, Veiculo.class);
    }
}
