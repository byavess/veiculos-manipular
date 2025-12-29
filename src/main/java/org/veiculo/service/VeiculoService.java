package org.veiculo.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.veiculo.model.dto.VeiculoRequest;
import org.veiculo.model.entity.Veiculo;
import org.veiculo.model.repository.VeiculoRepository;
import org.veiculo.security.JwtUtil;

import java.util.List;
import java.util.Objects;

import static org.veiculo.model.converter.VeiculoMapper.mapperVeiculoRequestParaVeiculo;
import static org.veiculo.util.PlacaUtil.mascararPlaca;

@Service
@RequiredArgsConstructor
public class VeiculoService {
    private final VeiculoRepository veiculoRepository;

    private List<Veiculo> getAllVeiculos() {
        List<Veiculo> veiculos = veiculoRepository.findAll();
        if (!JwtUtil.isAdmin()) {
            veiculos.forEach(veiculo -> veiculo.setPlaca(mascararPlaca(veiculo.getPlaca()))
            );
        }
        return veiculos;
    }


    public Veiculo getVeiculoById(Long id) {
        Veiculo veiculo = veiculoRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Veículo com ID " + id + " não encontrado.")
        );
        if (!JwtUtil.isAdmin()) {
            veiculo.setPlaca(mascararPlaca(veiculo.getPlaca()));
        }
        return veiculo;
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

    public Page<Veiculo> searchVeiculosPaginado(
            String q,
            String marca,
            String modelo,
            Integer anoMin,
            Integer anoMax,
            String sort,
            String direction,
            int page,
            int size,
            Boolean vendido // novo parâmetro
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));
        Specification<Veiculo> spec = Specification.where(null);

        if (q != null && !q.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("marca")), "%" + q.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("modelo")), "%" + q.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("descricao")), "%" + q.toLowerCase() + "%")
            ));
        }
        if (marca != null && !marca.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("marca")), marca.toLowerCase()));
        }
        if (modelo != null && !modelo.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("modelo")), modelo.toLowerCase()));
        }
        if (anoMin != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("ano"), anoMin));
        }
        if (anoMax != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("ano"), anoMax));
        }
        // Filtro por vendido: só aplica se informado
        if (vendido != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("vendido"), vendido));
        }
        Page<Veiculo> veiculos = veiculoRepository.findAll(spec, pageable);
        if (!JwtUtil.isAdmin()) {
            veiculos.forEach(veiculo -> veiculo.setPlaca(mascararPlaca(veiculo.getPlaca()))
            );
        }

        return veiculos;
    }


    public Veiculo salvar(@Valid VeiculoRequest veiculoRequest) {
        veiculoRepository.findByPlaca(veiculoRequest.getPlaca()).ifPresent(veiculo -> {
            ;
            throw new IllegalArgumentException("Veículo com placa " + veiculoRequest.getPlaca() + " já existe.");
        });
        return veiculoRepository.save(mapperVeiculoRequestParaVeiculo(veiculoRequest));
    }

    public Veiculo atualizar(Long id, VeiculoRequest veiculoRequest) {
        Veiculo veiculoExistente = veiculoRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Veículo com ID " + id + " não encontrado.")
        );

        if (!veiculoExistente.getPlaca().equals(veiculoRequest.getPlaca())) {
            veiculoRepository.findByPlaca(veiculoRequest.getPlaca()).ifPresent(veiculo -> {
                throw new IllegalArgumentException("Veículo com placa " + veiculoRequest.getPlaca() + " já existe.");
            });
        }

        Veiculo veiculoAtualizado = mapperVeiculoRequestParaVeiculo(veiculoRequest);
        veiculoAtualizado.setId(id);

        return veiculoRepository.save(veiculoAtualizado);
    }

    public void deletar(Long id) {
        Veiculo veiculoExistente = veiculoRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Veículo com ID " + id + " não encontrado.")
        );
        veiculoRepository.delete(veiculoExistente);
    }
}