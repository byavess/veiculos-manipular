// src/main/java/org.testeveiculos.veiculosapi/controller/VeiculoController.java

package org.veiculo.controller; // 🛑 PACOTE CONTROLLER


import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.veiculo.model.entity.Veiculo;
import org.veiculo.service.VeiculoService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/veiculos")
@CrossOrigin(origins = "*")
@Log4j2
public class VeiculoController {


private final VeiculoService veiculoService;
public VeiculoController (VeiculoService veiculoService){
    this.veiculoService = veiculoService;
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

    @GetMapping("/modelos")
    public List<String> getModelos(@RequestParam(name = "marca", required = false) String marca) {
        if (marca != null && !marca.isEmpty()) {
            return veiculoService.getModelosByMarca(marca);
        }
        return veiculoService.getAllModelos();
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

    @GetMapping()
    public Page<Veiculo> getVeiculosPaginado(
            @RequestParam(name = "marca", required = false) String marca,
            @RequestParam(name = "modelo", required = false) String modelo,
            @RequestParam(name = "anoMin", required = false) Integer anoMin,
            @RequestParam(name = "anoMax", required = false) Integer anoMax,
            @RequestParam(name = "sort", defaultValue = "preco") String sort,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "12") int size
    ) {
        String sortBy = "preco";
        if (sort.equalsIgnoreCase("menorValor")) sortBy = "preco";
        else if (sort.equalsIgnoreCase("maiorValor")) sortBy = "preco";
        else if (sort.equalsIgnoreCase("dataCadastro")) sortBy = "id";

        String dir = direction;
        if (sort.equalsIgnoreCase("maiorValor")) dir = "desc";
        else if (sort.equalsIgnoreCase("menorValor")) dir = "asc";

        return veiculoService.searchVeiculosPaginado(
                marca, modelo, anoMin, anoMax, sortBy, dir, page, size
        );
    }
}