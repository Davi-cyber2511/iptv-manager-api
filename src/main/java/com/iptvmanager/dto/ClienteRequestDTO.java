package com.iptvmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; // Para 'ativo' se for o caso
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteRequestDTO {

    @NotBlank(message = "O nome do cliente não pode estar em branco.")
    private String nome;

    @NotBlank(message = "O telefone do cliente não pode estar em branco.")
    private String telefone;

    @Email(message = "Formato de e-mail inválido")
    @Size(max = 255, message = "O e-mail deve ter no máximo 255 caracteres")
    private String email;

    @NotBlank(message = "O servidor IPTV não pode estar em branco.")
    private String servidorIptv;

    private String observacoes;

    @NotNull(message = "O status 'ativo' do cliente não pode ser nulo.")
    private Boolean ativo;
}