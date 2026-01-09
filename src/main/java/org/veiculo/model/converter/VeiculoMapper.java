package org.veiculo.model.converter;

import org.veiculo.model.dto.VeiculoRequest;
import org.veiculo.model.entity.Marca;
import org.veiculo.model.entity.Modelo;
import org.veiculo.model.entity.Veiculo;
import org.veiculo.model.repository.MarcaRepository;
import org.veiculo.model.repository.ModeloRepository;

public class VeiculoMapper {

    public static Veiculo mapperVeiculoRequestParaVeiculo(
            VeiculoRequest veiculoRequest,
            MarcaRepository marcaRepository,
            ModeloRepository modeloRepository
    ) {
        // Busca a marca pelo ID
        Marca marca = marcaRepository.findById(veiculoRequest.getMarca())
                .orElseThrow(() -> new IllegalArgumentException("Marca não encontrada com ID: " + veiculoRequest.getMarca()));

        // Busca o modelo pelo ID
        Modelo modelo = modeloRepository.findById(veiculoRequest.getModelo())
                .orElseThrow(() -> new IllegalArgumentException("Modelo não encontrado com ID: " + veiculoRequest.getModelo()));

        return Veiculo.builder()
                .marca(marca)
                .modelo(modelo)
                .ano(veiculoRequest.getAno())
                .km(veiculoRequest.getKm())
                .preco(veiculoRequest.getPreco())
                .descricao(veiculoRequest.getDescricao())
                .cor(veiculoRequest.getCor())
                .motor(veiculoRequest.getMotor())
                .cambio(veiculoRequest.getCambio())
                .combustivel(veiculoRequest.getCombustivel())
                .urlsFotos(veiculoRequest.getUrlsFotos())
                .emOferta(veiculoRequest.getEmOferta())
                .vendido(veiculoRequest.getVendido())
                .placa(veiculoRequest.getPlaca())
                .infoVenda(veiculoRequest.getInfoVenda())
                .build();
    }
}
