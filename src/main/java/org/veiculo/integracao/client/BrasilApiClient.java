package org.veiculo.integracao.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.veiculo.config.FeignClientConfig;
import org.veiculo.integracao.dto.MarcaFipeResponse;
import org.veiculo.integracao.dto.ModeloFipeResponse;

import java.util.List;

@FeignClient(name = "brasilApiFipe", url = "${integracao.brasilapi.url}", configuration = FeignClientConfig.class)
@Service
public interface BrasilApiClient {
    @GetMapping("/fipe/marcas/v1/{tipoVeiculo}")
    List<MarcaFipeResponse> getMarcasFipe(@PathVariable("tipoVeiculo") String tipoVeiculo);

    @GetMapping("/fipe/veiculos/v1/{tipoVeiculo}/{codigoMarca}")
    List<ModeloFipeResponse> getModelosFipe(@PathVariable("tipoVeiculo") String tipoVeiculo, @PathVariable("codigoMarca") String codigoMarca);
}
