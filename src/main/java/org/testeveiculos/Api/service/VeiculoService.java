package org.testeveiculos.Api.service;

import org.springframework.stereotype.Service;
import org.testeveiculos.Api.model.Veiculo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VeiculoService {

    private List<Veiculo> veiculos = new ArrayList<>();

    public VeiculoService() {
        // Inicializando com dados de exemplo baseados na sua estrutura de pastas
        veiculos.add(new Veiculo(1L, "BMW", "X1", 2023, 250000.00,
                "BMW X1 2023 - Luxo e performance","vermelha", List.of(
                        "/images/veiculos/bmw/bmw.webp",
                        "/images/veiculos/bmw/bmw1.webp",
                        "/images/veiculos/bmw/bmw2.webp"
                )));
        veiculos.add(new Veiculo(2L, "Renault", "Duster", 2023, 120000.00,
                "Renault Duster 2023 - SUV robusto","azul", List.of(
                        "/images/veiculos/duster/duster.webp",
                        "/images/veiculos/duster/duster1.webp",
                        "/images/veiculos/duster/duster2.webp"
                )));
        veiculos.add(new Veiculo(3L, "Ford", "EcoSport", 2023, 110000.00,
                "Ford EcoSport 2023 - Compacto e versátil","preta", List.of(
                        "/images/veiculos/ecoSport/ecosport.webp",
                        "/images/veiculos/ecoSport/ecosport1.webp",
                        "/images/veiculos/ecoSport/ecosport2.webp"
                )));
        veiculos.add(new Veiculo(4L, "Volkswagen", "Gol", 2023, 80000.00,
                "Volkswagen Gol 2023 - Clássico brasileiro","amarela", List.of(
                        "/images/veiculos/gol/gol.webp",
                        "/images/veiculos/gol/gol1.webp",
                        "/images/veiculos/gol/gol2.webp"
                )));
        veiculos.add(new Veiculo(5L, "Fiat", "Uno", 2023, 70000.00,
                "Fiat Uno 2023 - Econômico e prático","branca", List.of(
                        "/images/veiculos/uno/fiat.webp",
                        "/images/veiculos/uno/fiat1.webp",
                        "/images/veiculos/uno/fiat2.webp"
                )));
    }

    public List<Veiculo> getAllVeiculos() {
        return veiculos;
    }


    public Optional<Veiculo> getVeiculoById(Long id) {
        return veiculos.stream()
                .filter(veiculo -> veiculo.getId().equals(id))
                .findFirst();
    }

    public List<Veiculo> getVeiculosByMarca(String marca) {
        return veiculos.stream()
                .filter(veiculo -> veiculo.getMarca().equalsIgnoreCase(marca))
                .toList();
    }

    public List<String> getAllMarcas() {
        return veiculos.stream()
                .map(Veiculo::getMarca)
                .distinct()
                .toList();
    }

    public List<Veiculo> searchVeiculos(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return veiculos;
        }

        String termoLower = termo.toLowerCase();
        return veiculos.stream()
                .filter(veiculo ->
                        veiculo.getMarca().toLowerCase().contains(termoLower) ||
                                veiculo.getModelo().toLowerCase().contains(termoLower) ||
                                veiculo.getDescricao().toLowerCase().contains(termoLower))
                .toList();
    }


}