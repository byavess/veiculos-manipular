package org.veiculo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.veiculo.model.enums.Cambio;
import org.veiculo.model.enums.Combustivel;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoRequest {

    @NotBlank
    private String marca;
    @NotBlank
    private String modelo;
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
    @NotEmpty
    private List<String> urlsFotos;
    @NotNull
    private Boolean emOferta = false;
    @NotNull
    private Boolean vendido = false;
    @NotBlank
    private String placa;
}
