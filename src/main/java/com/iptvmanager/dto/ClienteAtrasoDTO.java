package com.iptvmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteAtrasoDTO {
    private String id;
    private String nome;
    private String telefone;
    private LocalDate dataVencimento;
    private BigDecimal valorUltimaRenovacao;
    private long diasEmAtraso; // Quantidade de dias desde o vencimento
}