// src/main/java/org.testeveiculos.veiculosapi/model/Veiculo.java

package org.testeveiculos.Api.model; // 🛑 PACOTE CORRETO

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Veiculo {
    private Long id;
    private String marca;
    private String modelo;
    private Integer ano;
    private Double preco;
    private String descricao;
    private String cor;
    private List<String> urlsFotos;


    public Veiculo(Long id, String marca, String modelo,
                   Integer ano, Double preco, String descricao,String cor, String urlsFotos) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.preco = preco;
        this.descricao = descricao;
        this.cor = "não informada";
        this.urlsFotos = Collections.singletonList(urlsFotos);

    }

    public Veiculo(int id, String marca, String modelo, int ano, double preco, String descricao, String cor, String urlsFotos ) {
    }
    // Adicione este método na classe Veiculo:


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public List<String> getImagem() { return urlsFotos; }
    public void setImagem(String imagem) { this.urlsFotos = Collections.singletonList(imagem); }


}
