package org.veiculo.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.veiculo.model.dto.VeiculoRequest;

public class InfoVendaValidator implements ConstraintValidator<ValidInfoVenda, VeiculoRequest> {

    @Override
    public boolean isValid(VeiculoRequest veiculoRequest, ConstraintValidatorContext context) {
        if (veiculoRequest == null) {
            return true;
        }

        // Se vendido = true, infoVenda deve estar preenchido
        if (Boolean.TRUE.equals(veiculoRequest.getVendido())) {
            if (veiculoRequest.getInfoVenda() == null || veiculoRequest.getInfoVenda().trim().isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("não deve estar em branco quando o veículo está marcado como vendido")
                        .addPropertyNode("infoVenda")
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}

