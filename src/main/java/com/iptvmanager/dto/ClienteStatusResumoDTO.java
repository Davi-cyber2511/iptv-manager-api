package com.iptvmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteStatusResumoDTO {

    private long ativos;

    private long vencendoHoje;

    private long proximoVencimento;

    private long vencidos;
}
