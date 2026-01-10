package org.veiculo.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.veiculo.model.dto.VeiculoRequest;
import org.veiculo.model.entity.Marca;
import org.veiculo.model.entity.Modelo;
import org.veiculo.model.entity.Veiculo;
import org.veiculo.model.repository.MarcaRepository;
import org.veiculo.model.repository.ModeloRepository;
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
    private final MarcaRepository marcaRepository;
    private final ModeloRepository modeloRepository;

    @Value("${app.images.directory:}")
    private String imagesDirectory;

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
                .filter(veiculo -> veiculo.getMarca().getNome().equalsIgnoreCase(marca))
                .toList();
    }

    public List<String> getAllMarcas() {
        return getAllVeiculos().stream()
                .map(Veiculo::getMarca)
                .map(Marca::getNome)
                .distinct()
                .toList();
    }

    public List<String> getAllModelos() {
        return getAllVeiculos().stream()
                .map(Veiculo::getModelo)
                .map(Modelo::getModelo)
                .distinct()
                .sorted()
                .toList();
    }

    public List<String> getModelosByMarca(String marca) {
        if (Objects.isNull(marca) || marca.trim().isEmpty()) {
            return getAllVeiculos().stream()
                    .map(Veiculo::getModelo)
                    .map(Modelo::getModelo)
                    .distinct()
                    .sorted()
                    .toList();
        }
        return getAllVeiculos().stream()
                .filter(veiculo -> veiculo.getMarca().getNome().equalsIgnoreCase(marca))
                .map(Veiculo::getModelo)
                .map(Modelo::getModelo)
                .distinct()
                .sorted()
                .toList();

    }

    public Page<Veiculo> searchVeiculosPaginado(
            String q,
            Long marcaId,
            Long modeloId,
            Integer anoMin,
            Integer anoMax,
            String sort,
            String direction,
            int page,
            int size,
            Boolean vendido
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));
        Specification<Veiculo> spec = Specification.where(null);

        if (q != null && !q.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("marca").get("nome")), "%" + q.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("modelo").get("modelo")), "%" + q.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("descricao")), "%" + q.toLowerCase() + "%")
            ));
        }
        if (marcaId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("marca").get("id"), marcaId));
        }
        if (modeloId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("modelo").get("id"), modeloId));
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

        // Valida limite máximo de 7 imagens
        if (veiculoRequest.getUrlsFotos() != null && veiculoRequest.getUrlsFotos().size() > 7) {
            throw new IllegalArgumentException("Limite máximo de 7 imagens por veículo.");
        }

        Veiculo veiculo = veiculoRepository.save(mapperVeiculoRequestParaVeiculo(veiculoRequest, marcaRepository, modeloRepository));

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

        // Valida limite máximo de 7 imagens
        if (veiculoRequest.getUrlsFotos() != null && veiculoRequest.getUrlsFotos().size() > 7) {
            throw new IllegalArgumentException("Limite máximo de 7 imagens por veículo.");
        }

        Veiculo veiculoAtualizado = mapperVeiculoRequestParaVeiculo(veiculoRequest, marcaRepository, modeloRepository);
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

            // Valida tamanho do arquivo (máximo 2MB)
            long maxTamanhoBytes = 2 * 1024 * 1024; // 2MB
            if (file.getSize() > maxTamanhoBytes) {
                throw new IllegalArgumentException("A imagem excede o tamanho máximo de 2MB.");
            }

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

            Path pastaDestino;
            Path pastaDestinoBackup = null;

            // Verifica se está em produção (com diretório configurado)
            if (imagesDirectory != null && !imagesDirectory.isEmpty()) {
                pastaDestino = Paths.get(imagesDirectory);
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



                // Usa caminhos absolutos baseados no diretório do projeto BACKEND
                pastaDestino = Paths.get(diretorioBase, "src", "main", "resources", "images", "veiculos");
                pastaDestinoBackup = Paths.get(diretorioBase, "target", "classes", "images", "veiculos");
            }

            // Garante que o diretório exista
            if (!Files.exists(pastaDestino)) {
                Files.createDirectories(pastaDestino);
            }

            if (pastaDestinoBackup != null && !Files.exists(pastaDestinoBackup)) {
                Files.createDirectories(pastaDestinoBackup);
            }

            // Salva a imagem
            Path destinoFinal = pastaDestino.resolve(nomeArquivo);

            // Usa Files.copy() ao invés de transferTo() para evitar problemas com caminhos absolutos no Windows
            Files.copy(file.getInputStream(), destinoFinal, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // Em desenvolvimento, copia também para target
            if (pastaDestinoBackup != null) {
                try {
                    Path destinoTarget = pastaDestinoBackup.resolve(nomeArquivo);
                    Files.copy(destinoFinal, destinoTarget, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    System.out.println("⚠️ Aviso: Não foi possível copiar para target/classes: " + e.getMessage());
                }
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
            Path pastaDestino;
            Path pastaDestinoBackup = null;

            // Verifica se está em produção (com diretório configurado)
            if (imagesDirectory != null && !imagesDirectory.isEmpty()) {
                // PRODUÇÃO: Usa o diretório configurado
                pastaDestino = Paths.get(imagesDirectory);
            } else {
                // DESENVOLVIMENTO: Usa a estrutura do projeto
                String diretorioBase = System.getProperty("user.dir");

                if (!diretorioBase.endsWith("veiculos-manipular")) {
                    Path caminhoBackend = Paths.get(diretorioBase).getParent().resolve("veiculos-manipular");
                    if (Files.exists(caminhoBackend)) {
                        diretorioBase = caminhoBackend.toString();
                    }
                }

                pastaDestino = Paths.get(diretorioBase, "src", "main", "resources", "images", "veiculos");
                pastaDestinoBackup = Paths.get(diretorioBase, "target", "classes", "images", "veiculos");
            }

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

                // Renomeia no diretório principal
                Path origem = pastaDestino.resolve(nomeAtual);
                Path destino = pastaDestino.resolve(novoNome);
                if (Files.exists(origem)) {
                    Files.move(origem, destino, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }

                // Em desenvolvimento, renomeia também no target
                if (pastaDestinoBackup != null) {
                    try {
                        Path origemTarget = pastaDestinoBackup.resolve(nomeAtual);
                        Path destinoTarget = pastaDestinoBackup.resolve(novoNome);
                        if (Files.exists(origemTarget)) {
                            Files.move(origemTarget, destinoTarget, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (Exception e) {
                        System.out.println("⚠️ Aviso ao renomear em target: " + e.getMessage());
                    }
                }

                novasUrls.add("veiculos/" + novoNome);
            }

            // Atualiza as URLs no banco
            Veiculo veiculo = veiculoRepository.findById(veiculoId).orElse(null);
            if (veiculo != null) {
                veiculo.setUrlsFotos(novasUrls);
                veiculoRepository.save(veiculo);
            }

        } catch (Exception e) {
            System.err.println("❌ Erro ao renomear imagens do veículo " + veiculoId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}