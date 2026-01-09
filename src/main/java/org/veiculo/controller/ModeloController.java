package org.veiculo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.veiculo.model.dto.ModeloResponse;
import org.veiculo.service.ModeloService;

import java.util.List;

@RestController
@RequestMapping("/api/modelos")
@Log4j2
@RequiredArgsConstructor
public class ModeloController {

    private final ModeloService modeloService;

    @GetMapping
    public ResponseEntity<List<ModeloResponse>> listarTodos(
            @RequestParam(name = "marcaId", required = false) Long marcaId) {

        if (marcaId != null) {
            log.info("Listando modelos da marca ID: {}", marcaId);
            return ResponseEntity.ok(modeloService.listarPorMarcaId(marcaId));
        }

        log.info("Listando todos os modelos");
        return ResponseEntity.ok(modeloService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModeloResponse> buscarPorId(@PathVariable Long id) {
        log.info("Buscando modelo por ID: {}", id);
        return ResponseEntity.ok(modeloService.buscarPorId(id));
    }
}

