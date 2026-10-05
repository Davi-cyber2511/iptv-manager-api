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
    private String email;
    private String servidorIptv;
    private String observacoes;
    private StatusCliente status;
    private LocalDate dataVencimento;
    private BigDecimal valor;

    public static ClienteResponseDTO fromEntity(Cliente cliente) {
        return ClienteResponseDTO.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .telefone(cliente.getTelefone())
                .email(cliente.getEmail())
                .servidorIptv(cliente.getServidorIptv())
                .observacoes(cliente.getObservacoes())
                .status(cliente.getStatus())
                .dataVencimento(cliente.getDataVencimentoUltimaRenovacao())
                .valor(cliente.getValorUltimaRenovacao())
                .build();
    }
}