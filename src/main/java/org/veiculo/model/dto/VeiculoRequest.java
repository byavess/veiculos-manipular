package org.veiculo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.veiculo.model.enums.Cambio;
import org.veiculo.model.enums.Combustivel;
import org.veiculo.model.validation.ValidInfoVenda;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidInfoVenda
public class VeiculoRequest {

    @NotNull
    private Long marca;
    @NotNull
    private Long modelo;
    @NotNull
    private Integer ano;
    @NotNull
    private Integer km;
    @NotNull
    private BigDecimal preco;
    @NotBlank
    private String descricao;
    @NotBlank
    private String cor;
    @NotBlank
    private String motor;
    @NotNull
    private Cambio cambio;
    @NotNull
    private Combustivel combustivel;
    private List<String> urlsFotos;
    @NotNull
    private Boolean emOferta = false;
    @NotNull
    private Boolean vendido;
    @NotBlank
    private String placa;
    private String infoVenda;
}

