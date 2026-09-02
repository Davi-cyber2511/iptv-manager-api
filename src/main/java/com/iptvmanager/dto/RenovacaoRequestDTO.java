package com.iptvmanager.dto;

import com.iptvmanager.domain.enums.UnidadeDuracao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class RenovacaoRequestDTO {

    @NotBlank(message = "O ID do cliente não pode estar em branco.")
    private String clienteId;

    @NotNull(message = "A data de início da renovação não pode ser nula.")
    private LocalDate dataInicio;

    @NotNull(message = "A quantidade de duração não pode ser nula.")
    @Positive(message = "A quantidade de duração deve ser um número positivo.")
    private Integer duracaoQuantidade;

    @NotNull(message = "A unidade de duração não pode ser nula.")
    private UnidadeDuracao duracaoUnidade;

    @NotNull(message = "O valor da renovação não pode ser nulo.")
    @Positive(message = "O valor da renovação deve ser um número positivo.")
    private BigDecimal valor;

    private String observacao;
}
