package org.veiculo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.veiculo.model.entity.Veiculo;
import org.veiculo.model.repository.VeiculoRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VeiculoService {
    private final VeiculoRepository veiculoRepository;

    public List<Veiculo> getAllVeiculos() {
        return veiculoRepository.findAll();
    }


    public Optional<Veiculo> getVeiculoById(Long id) {
        return Optional.ofNullable(veiculoRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Veículo com ID " + id + " não encontrado.")
        ));
    }

    public List<Veiculo> getVeiculosByMarca(String marca) {
        return getAllVeiculos().stream()
                .filter(veiculo -> veiculo.getMarca().equalsIgnoreCase(marca))
                .toList();
    }

    public List<String> getAllMarcas() {
        return getAllVeiculos().stream()
                .map(Veiculo::getMarca)
                .distinct()
                .toList();
    }

    public List<String> getAllModelos() {
        return getAllVeiculos().stream()
                .map(Veiculo::getModelo)
                .distinct()
                .sorted()
                .toList();
    }

    public List<String> getModelosByMarca(String marca) {
        if (Objects.isNull(marca) || marca.trim().isEmpty()) {
            return getAllVeiculos().stream()
                    .map(Veiculo::getModelo)
                    .distinct()
                    .sorted()
                    .toList();
        }
        return getAllVeiculos().stream()
                .filter(veiculo -> veiculo.getMarca().equalsIgnoreCase(marca))
                .map(Veiculo::getModelo)
                .distinct()
                .sorted()
                .toList();

    }

    public List<Veiculo> searchVeiculos(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return getAllVeiculos();
        }

        String termoLower = termo.toLowerCase();
        return getAllVeiculos().stream()
                .filter(veiculo ->
                        veiculo.getMarca().toLowerCase().contains(termoLower) ||
                                veiculo.getModelo().toLowerCase().contains(termoLower) ||
                                veiculo.getDescricao().toLowerCase().contains(termoLower))
                .toList();
    }

    public Page<Veiculo> searchVeiculosPaginado(
            String marca,
            String modelo,
            Integer anoMin,
            Integer anoMax,
            String sortBy,
            String direction,
            int page,
            int size
    ) {
        System.out.println("🔍 Filtros recebidos no Service:");
        System.out.println("   Marca: '" + marca + "' (isEmpty: " + (marca != null && marca.isEmpty()) + ")");
        System.out.println("   Modelo: '" + modelo + "' (isEmpty: " + (modelo != null && modelo.isEmpty()) + ")");
        System.out.println("   AnoMin: " + anoMin);
        System.out.println("   AnoMax: " + anoMax);

        Specification<Veiculo> spec = Specification.where(null);
        if (marca != null && !marca.isEmpty()) {
            System.out.println("✅ Aplicando filtro de MARCA: " + marca);
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("marca")), "%" + marca.toLowerCase() + "%"));
        }
        if (modelo != null && !modelo.isEmpty()) {
            System.out.println("✅ Aplicando filtro de MODELO: " + modelo);
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("modelo")), "%" + modelo.toLowerCase() + "%"));
        }
        if (anoMin != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("ano"), anoMin));
        }
        if (anoMax != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("ano"), anoMax));
        }
        // Sempre ordenar por ofertas primeiro (em_oferta DESC)
        Sort sort = Sort.by(Sort.Direction.DESC, "emOferta");

        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction dir = (direction != null && direction.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = sort.and(Sort.by(dir, sortBy));
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        return veiculoRepository.findAll(spec, pageable);
    }


}