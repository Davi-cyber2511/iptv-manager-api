package com.iptvmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; // Para 'ativo' se for o caso
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteRequestDTO {

    @NotBlank(message = "O nome do cliente não pode estar em branco.")
    private String nome;

    @NotBlank(message = "O telefone do cliente não pode estar em branco.")
    private String telefone;

    @NotBlank(message = "O servidor IPTV não pode estar em branco.")
    private String servidorIptv;

    private String observacoes;

    @NotNull(message = "O status 'ativo' do cliente não pode ser nulo.")
    private Boolean ativo; // Se você tiver um campo 'ativo' na entidade Cliente
}