package org.veiculo.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.veiculo.model.entity.Veiculo;
import org.veiculo.service.VeiculoService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/veiculos")
@Log4j2
@RequiredArgsConstructor
public class VeiculoController {


    private final VeiculoService veiculoService;

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
        log.info("🔍 [GET /imagens] Requisição recebida para: {}", path);
        try {
            if (path.contains("..")) {
                log.warn("⚠️ [GET /imagens] Path rejeitado (traversal attack): {}", path);
                return ResponseEntity.badRequest().build();
            }

            String normalized = path.replaceFirst("^/+", "").replaceFirst("^images/", "");
            log.info("📝 [GET /imagens] Path normalizado: {}", normalized);

            // Determina o diretório base do projeto backend
            String diretorioBase = System.getProperty("user.dir");
            log.info("🏠 [GET /imagens] Diretório de trabalho: {}", diretorioBase);

            // Se o diretório atual não for o backend, ajusta o caminho
            if (!diretorioBase.endsWith("veiculos-manipular")) {
                Path caminhoBackend = Paths.get(diretorioBase).getParent().resolve("veiculos-manipular");
                if (Files.exists(caminhoBackend)) {
                    diretorioBase = caminhoBackend.toString();
                    log.info("🔄 [GET /imagens] Ajustado para diretório backend: {}", diretorioBase);
                }
            }

            // Tenta primeiro no filesystem (para imagens recém-carregadas)
            Path filesystemPath = Paths.get(diretorioBase, "src", "main", "resources", "images", normalized);
            log.info("📁 [GET /imagens] Buscando no filesystem: {}", filesystemPath.toAbsolutePath());
            log.info("   - Existe? {}", Files.exists(filesystemPath));
            log.info("   - Legível? {}", Files.isReadable(filesystemPath));

            if (Files.exists(filesystemPath) && Files.isReadable(filesystemPath)) {
                try {
                    byte[] imageBytes = Files.readAllBytes(filesystemPath);
                    String contentType = determineContentType(normalized);
                    log.info("✅ [GET /imagens] Imagem encontrada no filesystem - {} bytes, tipo: {}",
                             imageBytes.length, contentType);
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .body(new org.springframework.core.io.ByteArrayResource(imageBytes));
                } catch (Exception e) {
                    log.error("❌ [GET /imagens] Erro ao ler do filesystem: {}", filesystemPath, e);
                }
            } else {
                log.info("ℹ️ [GET /imagens] Imagem não encontrada no filesystem, tentando classpath...");
            }

            // Fallback para classpath (para imagens existentes no JAR)
            String classpathLocation = "images/" + normalized;
            log.info("📦 [GET /imagens] Buscando no classpath: {}", classpathLocation);
            Resource resource = new ClassPathResource(classpathLocation);

            if (resource.exists() && resource.isReadable()) {
                String contentType = determineContentType(classpathLocation);
                log.info("✅ [GET /imagens] Imagem encontrada no classpath - tipo: {}", contentType);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                log.warn("❌ [GET /imagens] Imagem NÃO encontrada em filesystem nem classpath: {}", normalized);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("💥 [GET /imagens] Erro inesperado ao servir imagem: {}", path, e);
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
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "marca", required = false) String marca,
            @RequestParam(name = "modelo", required = false) String modelo,
            @RequestParam(name = "anoMin", required = false) Integer anoMin,
            @RequestParam(name = "anoMax", required = false) Integer anoMax,
            @RequestParam(name = "sort", defaultValue = "emOferta") String sort,
            @RequestParam(name = "direction", defaultValue = "desc") String direction,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "12") int size,
            @RequestParam(name = "vendido", required = false) Boolean vendido
    ) {
        return veiculoService.searchVeiculosPaginado(
                q, marca, modelo, anoMin, anoMax, sort, direction, page, size, vendido
        );
    }
}