package org.veiculo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.veiculo.model.converter.StringListConverter;
import org.veiculo.model.enums.Cambio;
import org.veiculo.model.enums.Combustivel;
import org.veiculo.model.enums.TipoVeiculo;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Veiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marca_id")
    @JsonIgnoreProperties({"modelos", "hibernateLazyInitializer", "handler"})
    private Marca marca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modelo_id")
    @JsonIgnoreProperties({"marca", "hibernateLazyInitializer", "handler"})
    private Modelo modelo;

    // ...existing code...

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

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoVeiculo tipoVeiculo;
}
