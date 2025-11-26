// src/main/java/org.testeveiculos.veiculosapi/model/Veiculo.java

package org.testeveiculos.Api.model; // 🛑 PACOTE CORRETO

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Veiculo {
    private Long id;
    private String marca;
    private String modelo;
    private Integer ano;
    private Double preco;
    private String cor;
    private List<String> urlsFotos;
    private String descricao;
}
