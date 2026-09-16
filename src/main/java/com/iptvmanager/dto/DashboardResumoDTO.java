package com.iptvmanager.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResumoDTO {

    private long totalClientes;
    private long clientesAtivos;
    private long clientesVencendoHoje;
    private long clientesProximoVencimento; // Ex: próximos 7 dias
    private long clientesVencidos;
    private long clientesSemRenovacao;
    private long clientesInativos;
    private long clientesAtrasoProlongado; // Novo campo para clientes com mais de 1 mês de atras
}
