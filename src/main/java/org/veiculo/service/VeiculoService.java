package org.veiculo.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.veiculo.model.dto.VeiculoRequest;
import org.veiculo.model.entity.Veiculo;
import org.veiculo.model.repository.VeiculoRepository;
import org.veiculo.security.JwtUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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
            throw new IllegalArgumentException("Veículo com placa " + veiculoRequest.getPlaca() + " já existe.");
        });
        Veiculo veiculo = veiculoRepository.save(mapperVeiculoRequestParaVeiculo(veiculoRequest));

        // Renomeia as imagens com o ID do veículo após salvar
        if (veiculo.getUrlsFotos() != null && !veiculo.getUrlsFotos().isEmpty()) {
            renomearImagensVeiculo(veiculo.getId(), veiculo.getUrlsFotos());
            // Recarrega o veículo com as URLs atualizadas
            veiculo = veiculoRepository.findById(veiculo.getId()).orElse(veiculo);
        }

        return veiculo;
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

        Veiculo veiculo = veiculoRepository.save(veiculoAtualizado);

        // Renomeia as imagens com o ID do veículo após atualizar
        if (veiculo.getUrlsFotos() != null && !veiculo.getUrlsFotos().isEmpty()) {
            renomearImagensVeiculo(veiculo.getId(), veiculo.getUrlsFotos());
            // Recarrega o veículo com as URLs atualizadas
            veiculo = veiculoRepository.findById(veiculo.getId()).orElse(veiculo);
        }

        return veiculo;
    }

    public void deletar(Long id) {
        Veiculo veiculoExistente = veiculoRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Veículo com ID " + id + " não encontrado.")
        );
        veiculoRepository.delete(veiculoExistente);
    }

    public String uploadImagem(MultipartFile file, Principal principal) {
        try {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Arquivo não é uma imagem válida.");
            }

            String extensao = Optional.ofNullable(file.getOriginalFilename())
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(f.lastIndexOf('.')))
                    .orElse(".jpg");

            // Gera nome único temporário (sem prefixo de ID ainda, será adicionado no frontend)
            String nomeArquivo = UUID.randomUUID() + extensao;

            // Determina o diretório base do projeto backend
            // Usa o diretório de trabalho ou tenta encontrar pelo classpath
            String diretorioBase = System.getProperty("user.dir");

            // Se o diretório atual não for o backend, ajusta o caminho
            if (!diretorioBase.endsWith("veiculos-manipular")) {
                // Está rodando do frontend, precisa ir para o backend
                Path caminhoBackend = Paths.get(diretorioBase).getParent().resolve("veiculos-manipular");
                if (Files.exists(caminhoBackend)) {
                    diretorioBase = caminhoBackend.toString();
                }
            }

            System.out.println("🔧 Diretório base detectado: " + diretorioBase);

            // Usa caminhos absolutos baseados no diretório do projeto BACKEND
            Path pastaDestinoSrc = Paths.get(diretorioBase, "src", "main", "resources", "images", "veiculos");
            Path pastaDestinoTarget = Paths.get(diretorioBase, "target", "classes", "images", "veiculos");

            System.out.println("📁 Salvando em SRC: " + pastaDestinoSrc.toAbsolutePath());
            System.out.println("📁 Copiando para TARGET: " + pastaDestinoTarget.toAbsolutePath());

            // Garante que os diretórios existam
            if (!Files.exists(pastaDestinoSrc)) {
                Files.createDirectories(pastaDestinoSrc);
                System.out.println("✅ Diretório SRC criado: " + pastaDestinoSrc);
            }
            if (!Files.exists(pastaDestinoTarget)) {
                Files.createDirectories(pastaDestinoTarget);
                System.out.println("✅ Diretório TARGET criado: " + pastaDestinoTarget);
            }

            // Salva no diretório src (principal para desenvolvimento)
            Path destinoSrc = pastaDestinoSrc.resolve(nomeArquivo);
            file.transferTo(destinoSrc.toFile());
            System.out.println("✅ Imagem salva em SRC: " + destinoSrc.toAbsolutePath());

            // Copia para target (usado em runtime)
            try {
                Path destinoTarget = pastaDestinoTarget.resolve(nomeArquivo);
                Files.copy(destinoSrc, destinoTarget, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.out.println("✅ Imagem copiada para TARGET: " + destinoTarget.toAbsolutePath());
            } catch (Exception e) {
                System.out.println("⚠️ Aviso: Não foi possível copiar para target/classes: " + e.getMessage());
            }

            return "veiculos/" + nomeArquivo;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("❌ Erro ao salvar imagem: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar imagem: " + e.getMessage(), e);
        }
    }

    /**
     * Renomeia imagens do veículo com prefixo do ID
     * Primeira imagem recebe prefixo "0-{id}", demais "{ordem}-{id}"
     */
    public void renomearImagensVeiculo(Long veiculoId, List<String> urlsFotos) {
        if (urlsFotos == null || urlsFotos.isEmpty()) {
            return;
        }

        try {
            // Determina o diretório base do projeto backend
            String diretorioBase = System.getProperty("user.dir");

            // Se o diretório atual não for o backend, ajusta o caminho
            if (!diretorioBase.endsWith("veiculos-manipular")) {
                Path caminhoBackend = Paths.get(diretorioBase).getParent().resolve("veiculos-manipular");
                if (Files.exists(caminhoBackend)) {
                    diretorioBase = caminhoBackend.toString();
                }
            }

            Path pastaDestinoSrc = Paths.get(diretorioBase, "src", "main", "resources", "images", "veiculos");
            Path pastaDestinoTarget = Paths.get(diretorioBase, "target", "classes", "images", "veiculos");

            List<String> novasUrls = new java.util.ArrayList<>();

            for (int i = 0; i < urlsFotos.size(); i++) {
                String urlAtual = urlsFotos.get(i);
                String nomeAtual = urlAtual.replace("veiculos/", "");

                // Pula se já tem prefixo de ID
                if (nomeAtual.matches("\\d+-" + veiculoId + "-.*")) {
                    novasUrls.add(urlAtual);
                    continue;
                }

                // Extrai extensão
                String extensao = nomeAtual.contains(".") ? nomeAtual.substring(nomeAtual.lastIndexOf('.')) : ".jpg";

                // Primeira imagem: prefixo "0-{id}", demais "{ordem}-{id}"
                String prefixo = i == 0 ? "0-" + veiculoId : i + "-" + veiculoId;
                String novoNome = prefixo + "-" + UUID.randomUUID().toString().substring(0, 8) + extensao;

                // Renomeia no src
                Path origemSrc = pastaDestinoSrc.resolve(nomeAtual);
                Path destinoSrc = pastaDestinoSrc.resolve(novoNome);
                if (Files.exists(origemSrc)) {
                    Files.move(origemSrc, destinoSrc, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("✅ Renomeado em SRC: " + nomeAtual + " → " + novoNome);
                }

                // Renomeia no target
                try {
                    Path origemTarget = pastaDestinoTarget.resolve(nomeAtual);
                    Path destinoTarget = pastaDestinoTarget.resolve(novoNome);
                    if (Files.exists(origemTarget)) {
                        Files.move(origemTarget, destinoTarget, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("✅ Renomeado em TARGET: " + nomeAtual + " → " + novoNome);
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ Aviso ao renomear em target: " + e.getMessage());
                }

                novasUrls.add("veiculos/" + novoNome);
            }

            // Atualiza as URLs no banco
            Veiculo veiculo = veiculoRepository.findById(veiculoId).orElse(null);
            if (veiculo != null) {
                veiculo.setUrlsFotos(novasUrls);
                veiculoRepository.save(veiculo);
                System.out.println("✅ URLs atualizadas no banco para veículo ID " + veiculoId);
            }

        } catch (Exception e) {
            System.err.println("❌ Erro ao renomear imagens do veículo " + veiculoId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}