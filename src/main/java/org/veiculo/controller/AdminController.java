package org.veiculo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.veiculo.model.dto.VeiculoRequest;
import org.veiculo.model.entity.Veiculo;
import org.veiculo.model.repository.VeiculoRepository;
import org.veiculo.service.VeiculoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final VeiculoService veiculoService;
    private final VeiculoRepository veiculoRepository;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        List<Veiculo> veiculos = veiculoRepository.findAll();
        long totalVeiculos = veiculos.size();
        long totalVendidos = veiculos.stream().filter(Veiculo::getVendido).count();
        long totalEmOferta = veiculos.stream().filter(Veiculo::getEmOferta).count();
        dashboard.put("totalVeiculos", totalVeiculos);
        dashboard.put("totalVendidos", totalVendidos);
        dashboard.put("totalEmOferta", totalEmOferta);
        return ResponseEntity.ok(dashboard);
    }

    @PostMapping("/veiculos")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createVeiculo(@Valid @RequestBody VeiculoRequest veiculoRequest) {
        try {
            Veiculo saved = veiculoService.salvar(veiculoRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao criar veículo: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/veiculos/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateVeiculo(@PathVariable("id") Long id, @RequestBody VeiculoRequest veiculoRequest) {
        try {
            Veiculo updated = veiculoService.atualizar(id, veiculoRequest);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao atualizar veículo: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/veiculos/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteVeiculo(@PathVariable("id") Long id) {
        try {
            veiculoService.deletar(id);
            return  ResponseEntity.noContent().build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao deletar veículo: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

}
