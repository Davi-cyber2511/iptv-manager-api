package com.iptvmanager.dto;

import com.iptvmanager.domain.Cliente;
import com.iptvmanager.domain.enums.StatusCliente;
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
public class ClienteResponseDTO {

    private String id;
    private String nome;
    private String telefone;
    private String servidorIptv;
    private String observacoes;
    private StatusCliente status; // O status já é calculado na entidade
    private LocalDate dataVencimento; // Data de vencimento da última renovação
    private BigDecimal valor; // Valor da última renovação

    public static ClienteResponseDTO fromEntity(Cliente cliente) {
        return ClienteResponseDTO.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .telefone(cliente.getTelefone())
                .servidorIptv(cliente.getServidorIptv())
                .observacoes(cliente.getObservacoes())
                .status(cliente.getStatus()) // Pega o status calculado
                .dataVencimento(cliente.getDataVencimentoUltimaRenovacao()) // Pega da última renovação
                .valor(cliente.getValorUltimaRenovacao()) // Pega da última renovação
                .build();
    }
}