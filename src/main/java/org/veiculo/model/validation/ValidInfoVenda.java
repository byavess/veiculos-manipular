package org.veiculo.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = InfoVendaValidator.class)
@Documented
public @interface ValidInfoVenda {
    String message() default "Informações de venda são obrigatórias quando o veículo está vendido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

