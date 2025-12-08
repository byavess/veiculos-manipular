package org.testeveiculos.Api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.testeveiculos.Api.model.converter.StringListConverter;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Veiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String marca;
    private String modelo;
    private Integer ano;

    @Column(precision = 10, scale = 2)
    private BigDecimal preco;

    private String descricao;
    private String cor;

    @Convert(converter = StringListConverter.class)
    @Column(name = "urls_fotos", columnDefinition = "TEXT")
    private List<String> urlsFotos;

}
