package com.iptvmanager.dto;

import com.iptvmanager.domain.Cliente;
import com.iptvmanager.domain.Renovacao;
import com.iptvmanager.domain.enums.StatusCliente;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ClienteResponseDTO {

    private String id;
    private String nome;
    private String telefone;
    private String servidorIptv;
    private String observacoes;
    private StatusCliente status;
    private LocalDate dataVencimento;
    private BigDecimal valor;

    public ClienteResponseDTO(
            String id,
            String nome,
            String telefone,
            String servidorIptv,
            String observacoes,
            StatusCliente status,
            LocalDate dataVencimento,
            BigDecimal valor
    ) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.servidorIptv = servidorIptv;
        this.observacoes = observacoes;
        this.status = status;
        this.dataVencimento = dataVencimento;
        this.valor = valor;
    }

    public static ClienteResponseDTO fromEntity(Cliente cliente) {
        Renovacao atual = cliente.getRenovacaoAtual();

        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.getServidorIptv(),
                cliente.getObservacoes(),
                cliente.getStatus(),
                atual != null ? atual.getDataVencimento() : null,
                atual != null ? atual.getValor() : null
        );
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getServidorIptv() {
        return servidorIptv;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public StatusCliente getStatus() {
        return status;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public BigDecimal getValor() {
        return valor;
    }
}
