package org.veiculo.tarefas;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.veiculo.integracao.client.BrasilApiClient;
import org.veiculo.model.entity.Marca;
import org.veiculo.model.entity.Modelo;
import org.veiculo.model.enums.TipoVeiculo;
import org.veiculo.model.repository.MarcaRepository;
import org.veiculo.model.repository.ModeloRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@ConditionalOnProperty(prefix = "carga.fipe", name = "enabled", havingValue = "true")
public class CargaMarcasEModelos {

    private final MarcaRepository marcaRepository;
    private final BrasilApiClient brasilApiClient;
    private final ModeloRepository modeloRepository;

    @PostConstruct
    public void executarCarga() {
        log.info("=== INICIANDO CARGA DE MARCAS E MODELOS ===");
        buscarMarcas();
        buscarModelos();
        log.info("=== CARGA FINALIZADA ===");
    }

    @Transactional
    public void buscarMarcas() {
        log.info("Buscando marcas da API...");
        var marcasFipe = brasilApiClient.getMarcasFipe(TipoVeiculo.CARRO.getDescricao());

        List<Marca> novasMarcas = new ArrayList<>();
        for (var marcaFipe : marcasFipe) {
            // Verifica se já existe pelo nome
            if (!marcaRepository.existsByNome(marcaFipe.getNome())) {
                novasMarcas.add(Marca.builder()
                        .nome(marcaFipe.getNome())
                        .valor(marcaFipe.getValor())
                        .build());
            }
        }

        if (!novasMarcas.isEmpty()) {
            marcaRepository.saveAll(novasMarcas);
            log.info("✓ {} marcas novas salvas", novasMarcas.size());
        } else {
            log.info("✓ Nenhuma marca nova para salvar");
        }
    }
    @Transactional
    public void buscarModelos() {
        log.info("Buscando modelos da API...");
        List<Marca> marcas = marcaRepository.findAll();

        if (marcas.isEmpty()) {
            log.warn("⚠️ Nenhuma marca encontrada no banco!");
            return;
        }

        // 1️⃣ Buscar todos os modelos existentes de uma vez
        List<Modelo> modelosExistentes = modeloRepository.findAll();
        log.info("Total de modelos já existentes no banco: {}", modelosExistentes.size());

        // Constante para tamanho do lote
        final int BATCH_SIZE = 100;

        // 3️⃣ Lista para armazenar os novos modelos (lote temporário)
        List<Modelo> loteModelos = new ArrayList<>();
        int totalProcessado = 0;
        int totalModelosApi = 0;
        int totalDuplicados = 0;
        int totalSalvos = 0;

        // 4️⃣ Buscar modelos de todas as marcas
        for (Marca marca : marcas) {
            try {
                var modelosFipe = brasilApiClient.getModelosFipe(
                        TipoVeiculo.CARRO.getDescricao(),
                        marca.getValor()
                );

                if (modelosFipe == null || modelosFipe.isEmpty()) {
                    log.debug("Nenhum modelo retornado para marca: {}", marca.getNome());
                    continue;
                }

                totalModelosApi += modelosFipe.size();

                // 5️⃣ Filtrar e adicionar novos modelos ao lote
                for (var modeloFipe : modelosFipe) {

                    // Verifica se já existe no banco
                    boolean jaExiste = modelosExistentes.stream()
                            .anyMatch(m -> m.getMarca().getId().equals(marca.getId())
                                    && m.getModelo().trim().equalsIgnoreCase(modeloFipe.getModelo().trim()));

                    if (!jaExiste) {
                        Modelo novoModelo = Modelo.builder()
                                .modelo(modeloFipe.getModelo())
                                .marca(marca)
                                .build();

                        loteModelos.add(novoModelo);

                        // 6️⃣ Salvar em lotes de 100
                        if (loteModelos.size() >= BATCH_SIZE) {
                            modeloRepository.saveAll(loteModelos);
                            totalSalvos += loteModelos.size();
                            log.info("💾 Salvos {} modelos (total: {})", loteModelos.size(), totalSalvos);

                            // Adicionar à lista de existentes para evitar duplicatas nas próximas iterações
                            modelosExistentes.addAll(loteModelos);

                            // Limpar o lote
                            loteModelos.clear();
                        }
                    } else {
                        totalDuplicados++;
                    }
                }

                totalProcessado++;
                if (totalProcessado % 20 == 0) {
                    log.info("Processadas {}/{} marcas... ({} modelos salvos até agora)",
                            totalProcessado, marcas.size(), totalSalvos);
                }

            } catch (Exception e) {
                log.error("❌ Erro ao buscar modelos da marca {}: {}",
                        marca.getNome(), e.getMessage(), e);
            }
        }

        // 7️⃣ Salvar o resto que ficou no lote (menos de 100)
        if (!loteModelos.isEmpty()) {
            modeloRepository.saveAll(loteModelos);
            totalSalvos += loteModelos.size();
            log.info("💾 Salvos últimos {} modelos", loteModelos.size());
        }

        // 8️⃣ Resumo final
        log.info("=== RESUMO DA BUSCA DE MODELOS ===");
        log.info("Marcas processadas: {}", totalProcessado);
        log.info("Total de modelos retornados pela API: {}", totalModelosApi);
        log.info("Modelos duplicados ignorados: {}", totalDuplicados);
        log.info("✅ Total de modelos salvos: {}", totalSalvos);
    }



}

