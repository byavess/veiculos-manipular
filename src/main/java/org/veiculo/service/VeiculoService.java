package org.veiculo.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.veiculo.model.converter.VeiculoMapper.mapperVeiculoRequestParaVeiculo;
import static org.veiculo.util.PlacaUtil.mascararPlaca;

@Service
@RequiredArgsConstructor
@Log4j2
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

        // Identifica e deleta imagens removidas
        if (veiculoExistente.getUrlsFotos() != null && !veiculoExistente.getUrlsFotos().isEmpty()) {
            List<String> urlsAntigas = veiculoExistente.getUrlsFotos();
            List<String> urlsNovas = veiculoRequest.getUrlsFotos() != null ? veiculoRequest.getUrlsFotos() : List.of();

            // Encontra URLs que foram removidas
            List<String> urlsRemovidas = urlsAntigas.stream()
                    .filter(urlAntiga -> !urlsNovas.contains(urlAntiga))
                    .toList();

            // Deleta as imagens removidas do diretório
            if (!urlsRemovidas.isEmpty() && imagesDirectory != null && !imagesDirectory.isEmpty()) {
                Path pastaDestino = Paths.get(imagesDirectory);
                if (Files.exists(pastaDestino)) {
                    for (String urlRemovida : urlsRemovidas) {
                        String nomeArquivo = urlRemovida.replace("veiculos/", "");
                        Path caminhoImagem = pastaDestino.resolve(nomeArquivo);
                        try {
                            Files.deleteIfExists(caminhoImagem);
                        } catch (Exception e) {
                            log.error("⚠\uFE0F Não foi possível deletar a imagem: {} - {}", caminhoImagem, e.getMessage());
                        }
                    }
                }
            }
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

        if (imagesDirectory == null || imagesDirectory.isEmpty()) {
            throw new IllegalStateException("Diretório de imagens não configurado. Defina a propriedade 'app.images.directory'.");
        }

        Path pastaDestino = Paths.get(imagesDirectory);

        if (!Files.exists(pastaDestino)) {
            throw new IllegalStateException("Diretório de imagens não existe: " + imagesDirectory);
        }

        // Deleta as imagens associadas ao veículo
        if (veiculoExistente.getUrlsFotos() != null) {
            for (String url : veiculoExistente.getUrlsFotos()) {
                String nomeArquivo = url.replace("veiculos/", "");
                Path caminhoImagem = pastaDestino.resolve(nomeArquivo);
                try {
                    Files.deleteIfExists(caminhoImagem);
                } catch (Exception e) {
                    log.error("⚠\uFE0F Não foi possível deletar a imagem: {} - {}", caminhoImagem, e.getMessage());
                }
            }
        }

        veiculoRepository.delete(veiculoExistente);
    }


    public String uploadImagem(MultipartFile file) {
        try {
            // Valida se o diretório está configurado
            if (imagesDirectory == null || imagesDirectory.isEmpty()) {
                throw new IllegalStateException("Diretório de imagens não configurado. Defina a propriedade 'app.images.directory'.");
            }

            Path pastaDestino = Paths.get(imagesDirectory);

            // Verifica se o diretório existe
            if (!Files.exists(pastaDestino)) {
                throw new IllegalStateException("Diretório de imagens não existe: " + imagesDirectory);
            }

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

            String nomeArquivo = UUID.randomUUID() + extensao;
            Path destinoFinal = pastaDestino.resolve(nomeArquivo);
            Files.copy(file.getInputStream(), destinoFinal, StandardCopyOption.REPLACE_EXISTING);

            return "veiculos/" + nomeArquivo;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Erro ao salvar imagem: {}", e.getMessage());
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

        if (imagesDirectory == null || imagesDirectory.isEmpty()) {
            throw new IllegalStateException("Diretório de imagens não configurado. Defina a propriedade 'app.images.directory'.");
        }

        Path pastaDestino = Paths.get(imagesDirectory);

        if (!Files.exists(pastaDestino)) {
            throw new IllegalStateException("Diretório de imagens não existe: " + imagesDirectory);
        }

        try {
            List<String> novasUrls = new java.util.ArrayList<>();

            for (int i = 0; i < urlsFotos.size(); i++) {
                String urlAtual = urlsFotos.get(i);
                String nomeAtual = urlAtual.replace("veiculos/", "");

                if (nomeAtual.matches("\\d+-" + veiculoId + "-.*")) {
                    novasUrls.add(urlAtual);
                    continue;
                }

                String extensao = nomeAtual.contains(".") ? nomeAtual.substring(nomeAtual.lastIndexOf('.')) : ".jpg";
                String prefixo = i == 0 ? "0-" + veiculoId : i + "-" + veiculoId;
                String novoNome = prefixo + "-" + UUID.randomUUID().toString().substring(0, 8) + extensao;

                Path origem = pastaDestino.resolve(nomeAtual);
                Path destino = pastaDestino.resolve(novoNome);
                if (Files.exists(origem)) {
                    Files.move(origem, destino, StandardCopyOption.REPLACE_EXISTING);
                }

                novasUrls.add("veiculos/" + novoNome);
            }

            Veiculo veiculo = veiculoRepository.findById(veiculoId).orElse(null);
            if (veiculo != null) {
                veiculo.setUrlsFotos(novasUrls);
                veiculoRepository.save(veiculo);
            }

        } catch (Exception e) {
            System.err.println("❌ Erro ao renomear imagens do veículo " + veiculoId + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao renomear imagens: " + e.getMessage(), e);
        }
    }

}