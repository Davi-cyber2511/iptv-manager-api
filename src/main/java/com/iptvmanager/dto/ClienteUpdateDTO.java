package com.iptvmanager.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteUpdateDTO {

    @NotBlank(message = "O nome não pode estar em branco")
    @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
    private String nome;

    @NotBlank(message = "O telefone não pode estar em branco")
    @Pattern(regexp = "^\\d{10,11}$", message = "O telefone deve conter 10 ou 11 dígitos numéricos")
    private String telefone;

    @NotBlank(message = "O servidor IPTV não pode estar em branco")
    @Size(max = 255, message = "O servidor IPTV deve ter no máximo 255 caracteres")
    private String servidorIptv;

    private String observacoes;

    private Boolean ativo; // Usamos Boolean para permitir null, indicando que não será atualizado se não for fornecido
}
