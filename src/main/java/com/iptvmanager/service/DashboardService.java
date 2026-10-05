package com.iptvmanager.service;

import com.iptvmanager.domain.Cliente;
import com.iptvmanager.domain.Usuario;
import com.iptvmanager.domain.enums.StatusCliente;
import com.iptvmanager.dto.ClienteAtrasoDTO;
import com.iptvmanager.dto.DashboardResumoDTO;
import com.iptvmanager.exception.ResourceNotFoundException;
import com.iptvmanager.repository.ClienteRepository;
import com.iptvmanager.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class DashboardService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    public DashboardService(
            ClienteRepository clienteRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public DashboardResumoDTO getDashboardResumo() {
        List<Cliente> clientes = listarClientesDoUsuario();

        long totalClientes = clientes.size();
        long ativos = 0;
        long vencendoHoje = 0;
        long proximoVencimento = 0;
        long vencidos = 0;
        long semRenovacao = 0;
        long inativos = 0;
        long atrasoProlongado = 0;

        LocalDate umMesAtras = LocalDate.now().minusMonths(1);

        for (Cliente cliente : clientes) {
            StatusCliente status = cliente.getStatus();

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
                    if (cliente.getDataVencimentoUltimaRenovacao() != null
                            && cliente.getDataVencimentoUltimaRenovacao()
                            .isBefore(umMesAtras)) {
                        atrasoProlongado++;
                    }
                    break;
                case SEM_RENOVACAO:
                    semRenovacao++;
                    break;
                case INATIVO:
                    inativos++;
                    break;
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
        LocalDate hoje = LocalDate.now();
        LocalDate umMesAtras = hoje.minusMonths(1);

        return listarClientesDoUsuario().stream()
                .filter(cliente -> Boolean.TRUE.equals(cliente.getAtivo()))
                .filter(cliente -> cliente.getStatus() == StatusCliente.VENCIDO)
                .filter(cliente ->
                        cliente.getDataVencimentoUltimaRenovacao() != null
                                && cliente.getDataVencimentoUltimaRenovacao()
                                .isBefore(umMesAtras)
                )
                .map(cliente -> {
                    LocalDate dataVencimento =
                            cliente.getDataVencimentoUltimaRenovacao();
                    long diasEmAtraso =
                            ChronoUnit.DAYS.between(dataVencimento, hoje);

                    return ClienteAtrasoDTO.builder()
                            .id(cliente.getId())
                            .nome(cliente.getNome())
                            .telefone(cliente.getTelefone())
                            .dataVencimento(dataVencimento)
                            .valorUltimaRenovacao(
                                    cliente.getValorUltimaRenovacao()
                            )
                            .diasEmAtraso(diasEmAtraso)
                            .build();
                })
                .toList();
    }

    private List<Cliente> listarClientesDoUsuario() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário autenticado não encontrado."
                ));

        return clienteRepository.findAllByUsuario_Id(usuario.getId());
    }
}