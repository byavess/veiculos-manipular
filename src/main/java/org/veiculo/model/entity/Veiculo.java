package org.veiculo.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.veiculo.model.converter.StringListConverter;
import org.veiculo.model.enums.Cambio;
import org.veiculo.model.enums.Combustivel;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Veiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String marca;
    private String modelo;
    private Integer ano;
    private Integer km;

    @Column(precision = 10, scale = 2)
    private BigDecimal preco;

    private String descricao;
    private String cor;
    private String motor;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Cambio cambio;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Combustivel combustivel;

    @Convert(converter = StringListConverter.class)
    @Column(name = "urls_fotos", columnDefinition = "TEXT")
    private List<String> urlsFotos;

    @Column(name = "em_oferta")
    private Boolean emOferta = false;

    @Column(name = "vendido")
    private Boolean vendido = false;

    @Column(unique = true)
    private String placa;

    @Column(name = "info_venda", columnDefinition = "TEXT")
    private String infoVenda;
}
