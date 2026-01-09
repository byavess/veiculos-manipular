package org.veiculo.model.enums;

import lombok.Getter;

@Getter
public enum TipoVeiculo {
    CARRO("carros"),
    MOTO("motos"),
    CAMINHAO("caminhoes");

    private final String descricao;

    TipoVeiculo(String descricao) {
        this.descricao = descricao;
    }
}
