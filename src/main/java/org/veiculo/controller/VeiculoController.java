package org.veiculo.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.veiculo.model.dto.PageResponse;
import org.veiculo.model.entity.Veiculo;
import org.veiculo.service.VeiculoService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/veiculos")
@RequiredArgsConstructor
@Log4j2
public class VeiculoController {

    private final VeiculoService veiculoService;

    @Value("${app.images.directory:}")
    private String imagesDirectory;

    @GetMapping("/{id}")
    public Veiculo getVeiculoById(@PathVariable("id") Long id) {
        return veiculoService.getVeiculoById(id);
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


    @GetMapping("/imagens")
    public ResponseEntity<Resource> getImagem(@RequestParam("path") String path) {
        try {
            if (path.contains("..")) {
                return ResponseEntity.badRequest().build();
            }

            String normalized = path.replaceFirst("^/+", "").replaceFirst("^images/", "");

            Path filesystemPath = null;

            // Verifica se está em produção (com diretório configurado)
            if (imagesDirectory != null && !imagesDirectory.isEmpty()) {
                // PRODUÇÃO: Usa o diretório configurado
                filesystemPath = Paths.get(imagesDirectory, normalized.replace("veiculos/", ""));
            } else {
                // DESENVOLVIMENTO: Usa a estrutura do projeto
                String diretorioBase = System.getProperty("user.dir");

                // Se o diretório atual não for o backend, ajusta o caminho
                if (!diretorioBase.endsWith("veiculos-manipular")) {
                    Path caminhoBackend = Paths.get(diretorioBase).getParent().resolve("veiculos-manipular");
                    if (Files.exists(caminhoBackend)) {
                        diretorioBase = caminhoBackend.toString();
                    }
                }

                // Tenta primeiro no filesystem (para imagens recém-carregadas)
                filesystemPath = Paths.get(diretorioBase, "src", "main", "resources", "images", normalized);

            }


            if (Files.exists(filesystemPath) && Files.isReadable(filesystemPath)) {
                try {
                    byte[] imageBytes = Files.readAllBytes(filesystemPath);
                    String contentType = determineContentType(normalized);
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .body(new org.springframework.core.io.ByteArrayResource(imageBytes));
                } catch (Exception e) {
                    log.error("❌ [GET /imagens] Erro ao ler do filesystem: {}", filesystemPath, e);
                }
            }
            if (imagesDirectory == null || imagesDirectory.isEmpty()) {
                String classpathLocation = "images/" + normalized;
                Resource resource = new ClassPathResource(classpathLocation);

                if (resource.exists() && resource.isReadable()) {
                    String contentType = determineContentType(classpathLocation);
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .body(resource);
                }
            }

            return ResponseEntity.notFound().build();
        } catch (Exception e) {
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
    public PageResponse<Veiculo> getVeiculosPaginado(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "marcaId", required = false) Long marcaId,
            @RequestParam(name = "modeloId", required = false) Long modeloId,
            @RequestParam(name = "anoMin", required = false) Integer anoMin,
            @RequestParam(name = "anoMax", required = false) Integer anoMax,
            @RequestParam(name = "sort", defaultValue = "emOferta") String sort,
            @RequestParam(name = "direction", defaultValue = "desc") String direction,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "12") int size,
            @RequestParam(name = "vendido", required = false) Boolean vendido
    ) {
        Page<Veiculo> pageResult = veiculoService.searchVeiculosPaginado(
                q, marcaId, modeloId, anoMin, anoMax, sort, direction, page, size, vendido
        );

        return new PageResponse<>(
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isFirst(),
                pageResult.isLast(),
                sort,
                direction
        );
    }
}

