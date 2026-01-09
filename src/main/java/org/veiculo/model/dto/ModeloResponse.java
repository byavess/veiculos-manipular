package org.veiculo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModeloResponse {
    private Long id;
    private String modelo;
    private Long marcaId;
    private String marcaNome;
}

