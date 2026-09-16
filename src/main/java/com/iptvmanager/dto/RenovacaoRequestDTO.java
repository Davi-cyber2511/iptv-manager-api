package com.iptvmanager.dto;

import com.iptvmanager.domain.enums.UnidadeDuracao;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RenovacaoRequestDTO {

    @NotNull(message = "A data de início não pode ser nula")
    private LocalDate dataInicio;

    @NotNull(message = "A duração da quantidade não pode ser nula")
    @Min(value = 1, message = "A duração da quantidade deve ser no mínimo 1")
    private Integer duracaoQuantidade;

    @NotNull(message = "A unidade de duração não pode ser nula")
    private UnidadeDuracao duracaoUnidade;

    @NotNull(message = "O valor não pode ser nulo")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal valor;

    private String observacao;
}
