// src/main/java/org.testeveiculos.veiculosapi/service/VeiculoService.java

package org.testeveiculos.Api.service; // 🛑 PACOTE SERVICE


import org.springframework.stereotype.Service;

import org.testeveiculos.Api.model.Veiculo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class VeiculoService {

    private final List<Veiculo> veiculos = new ArrayList<>();

    public VeiculoService() {
        // Dados fictícios (um de cada marca)
        veiculos.addAll(Arrays.asList(
                new Veiculo(
                        1L,
                        "BMW",
                        "X5",
                        2022,
                        180000.00,
                        "Preto", // <--- Cor
                        List.of("https://via.placeholder.com/300x200?text=BMW+X5"), // <-- urlsFotos (List<String>)
                        "Descrição"),
        new Veiculo(
                1L,
                "volkswagem",
                "jetta",
                2022,
                180000.00,
                "Preto", // <--- Cor
                List.of("https://via.placeholder.com/300x200?text=volkswagem+jetta"), // <-- urlsFotos (List<String>)
                "Descrição"),
        new Veiculo(
                1L,
                "fiat",
                "pulser",
                2022,
                180000.00,
                "Preto", // <--- Cor
                List.of("https://via.placeholder.com/300x200?text=ford+pulser"), // <-- urlsFotos (List<String>)
                "Descrição"),
                new Veiculo(
                        1L,
                        "ford",
                        "ranger",
                        2022,
                        180000.00,
                        "Preto", // <--- Cor
                        List.of("https://via.placeholder.com/300x200?text=ford+ranger"), // <-- urlsFotos (List<String>)
                        "Descrição"),new Veiculo(
                        1L,
                        "chevrolet",
                        "cruze",
                        2022,
                        180000.00,
                        "Preto", // <--- Cor
                        List.of("https://via.placeholder.com/300x200?text=chevrolet+cruze"), // <-- urlsFotos (List<String>)
                        "Descrição")
                // Adicione mais veículos conforme o seu frontend precisar!
        ));
    }

    public List<Veiculo> findAll() {
        return veiculos;
    }

    public Optional<Veiculo> findById(Long id) {
        return veiculos.stream()
                .filter(veiculo -> veiculo.getId().equals(id))
                .findFirst();
    }
}