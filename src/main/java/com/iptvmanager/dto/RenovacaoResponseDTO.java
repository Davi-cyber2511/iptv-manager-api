package com.iptvmanager.dto;

import com.iptvmanager.domain.Renovacao;
import com.iptvmanager.domain.enums.UnidadeDuracao;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class RenovacaoResponseDTO {

    private String id;
    private String clienteId; // ID do cliente associado
    private LocalDate dataInicio;
    private Integer duracaoQuantidade;
    private UnidadeDuracao duracaoUnidade;
    private LocalDate dataVencimento;
    private BigDecimal valor;
    public static RenovacaoResponseDTO fromEntity(Renovacao renovacao) {
        return RenovacaoResponseDTO.builder()
                .id(renovacao.getId())
                .clienteId(renovacao.getCliente() != null ? renovacao.getCliente().getId() : null)
                .dataInicio(renovacao.getDataInicio())
                .duracaoQuantidade(renovacao.getDuracaoQuantidade())
                .duracaoUnidade(renovacao.getDuracaoUnidade())
                .dataVencimento(renovacao.getDataVencimento())
                .valor(renovacao.getValor())
                .build();
    }
}
