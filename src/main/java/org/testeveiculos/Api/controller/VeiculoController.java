// src/main/java/org.testeveiculos.veiculosapi/controller/VeiculoController.java

package org.testeveiculos.Api.controller; // 🛑 PACOTE CONTROLLER


import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.testeveiculos.Api.model.Veiculo;
import org.testeveiculos.Api.service.VeiculoService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/veiculos")
@CrossOrigin(origins = "*") // 🛑 Essencial para o Angular
@Log4j2
public class VeiculoController {


private final VeiculoService veiculoService;
public VeiculoController (VeiculoService veiculoService){
    this.veiculoService = veiculoService;
}

    @GetMapping
    public List<Veiculo> getAllVeiculos() {
        return veiculoService.getAllVeiculos();
    }

    @GetMapping("/{id}")
    public Veiculo getVeiculoById(@PathVariable("id") Long id) {
        Optional<Veiculo> veiculo = veiculoService.getVeiculoById(id);
        return veiculo.orElse(null);
    }

    @GetMapping("/marca/{marca}")
    public List<Veiculo> getVeiculosByMarca(@PathVariable("marca") String marca) {

        return veiculoService.getVeiculosByMarca(marca);
    }

    @GetMapping("/marcas")
    public List<String> getAllMarcas() {
        return veiculoService.getAllMarcas();
    }

    @GetMapping("/search")
    public List<Veiculo> searchVeiculos(@RequestParam(required = false) String q) {
        return veiculoService.searchVeiculos(q);
    }


    @GetMapping("/imagens")
    public ResponseEntity<Resource> getImagem(@RequestParam("path") String path) {
        try {
            if (path.contains("..")) {
                log.warn("Imagem path rejected (traversal): {}", path);
                return ResponseEntity.badRequest().build();
            }

            String normalized = path.replaceFirst("^/+", "").replaceFirst("^images/", "");
            String classpathLocation = "images/" + normalized;

            Resource resource = new ClassPathResource(classpathLocation);
            boolean exists = resource.exists();
            boolean readable = resource.isReadable();


            if (exists && readable) {
                String contentType = determineContentType(classpathLocation);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                log.warn("Imagem not found: {}", classpathLocation);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Erro ao servir imagem: {}", path, e);
            return ResponseEntity.internalServerError().build();
        }
    }


    private String determineContentType(String filename) {
        if (filename.endsWith(".webp")) return "image/webp";
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
        if (filename.endsWith(".png")) return "image/png";
        return "application/octet-stream";
    }
}