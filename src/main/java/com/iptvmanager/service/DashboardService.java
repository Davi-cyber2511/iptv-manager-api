package com.iptvmanager.service;

import com.iptvmanager.domain.Cliente;
import com.iptvmanager.domain.enums.StatusCliente;
import com.iptvmanager.dto.ClienteAtrasoDTO;
import com.iptvmanager.dto.DashboardResumoDTO;
import com.iptvmanager.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class DashboardService {

    private final ClienteRepository clienteRepository;

    @Autowired
    public DashboardService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public DashboardResumoDTO getDashboardResumo() {
        List<Cliente> todosClientes = clienteRepository.findAll();

        long totalClientes = todosClientes.size();
        long ativos = 0;
        long vencendoHoje = 0;
        long proximoVencimento = 0;
        long vencidos = 0;
        long semRenovacao = 0;
        long inativos = 0;
        long atrasoProlongado = 0; // Inicializa o contador para o novo campo

        LocalDate hoje = LocalDate.now();
        LocalDate umMesAtras = hoje.minusMonths(1); // Data para verificar atraso prolongado

        for (Cliente cliente : todosClientes) {
            StatusCliente status = cliente.getStatus(); // Usa o método getStatus da entidade Cliente

            switch (status) {
                case ATIVO:
                    ativos++;
                    break;
                case VENCENDO_HOJE:
                    vencendoHoje++;
                    break;
                case PROXIMO_VENCIMENTO:
                    proximoVencimento++;
                    break;
                case VENCIDO:
                    vencidos++;
                    // Verifica se o cliente vencido está em atraso prolongado
                    if (cliente.getDataVencimentoUltimaRenovacao() != null && cliente.getDataVencimentoUltimaRenovacao().isBefore(umMesAtras)) {
                        atrasoProlongado++;
                    }
                    break;
                case SEM_RENOVACAO:
                    semRenovacao++;
                    break;
                case INATIVO:
                    inativos++;
                    break;
                // PENDENTE não precisa ser contado no dashboard, a menos que haja uma necessidade específica
            }
        }

        return DashboardResumoDTO.builder()
                .totalClientes(totalClientes)
                .clientesAtivos(ativos)
                .clientesVencendoHoje(vencendoHoje)
                .clientesProximoVencimento(proximoVencimento)
                .clientesVencidos(vencidos)
                .clientesSemRenovacao(semRenovacao)
                .clientesInativos(inativos)
                .clientesAtrasoProlongado(atrasoProlongado)
                .build();
    }

    public List<ClienteAtrasoDTO> getClientesAtrasoProlongado() {
        List<Cliente> todosClientes = clienteRepository.findAll();
        LocalDate hoje = LocalDate.now();
        LocalDate umMesAtras = hoje.minusMonths(1);

        return todosClientes.stream()
                .filter(cliente -> cliente.getAtivo() && cliente.getStatus() == StatusCliente.VENCIDO) // Apenas clientes ativos e vencidos
                .filter(cliente -> cliente.getDataVencimentoUltimaRenovacao() != null && cliente.getDataVencimentoUltimaRenovacao().isBefore(umMesAtras))
                .map(cliente -> {
                    LocalDate dataVencimento = cliente.getDataVencimentoUltimaRenovacao();
                    long diasEmAtraso = 0;
                    if (dataVencimento != null) {
                        diasEmAtraso = ChronoUnit.DAYS.between(dataVencimento, hoje);
                    }
                    return ClienteAtrasoDTO.builder()
                            .id(cliente.getId())
                            .nome(cliente.getNome())
                            .telefone(cliente.getTelefone())
                            .dataVencimento(dataVencimento)
                            .valorUltimaRenovacao(cliente.getValorUltimaRenovacao())
                            .diasEmAtraso(diasEmAtraso)
                            .build();
                })
                .toList(); // Use .collect(Collectors.toList()) se estiver em Java 8/11
    }
}