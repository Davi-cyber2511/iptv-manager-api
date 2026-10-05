package com.iptvmanager.service;

import com.iptvmanager.domain.Cliente;
import com.iptvmanager.domain.Renovacao;
import com.iptvmanager.domain.Usuario;
import com.iptvmanager.dto.RenovacaoRequestDTO;
import com.iptvmanager.dto.RenovacaoResponseDTO;
import com.iptvmanager.exception.ResourceNotFoundException;
import com.iptvmanager.repository.ClienteRepository;
import com.iptvmanager.repository.RenovacaoRepository;
import com.iptvmanager.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RenovacaoService {

    private final RenovacaoRepository renovacaoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    public RenovacaoService(
            RenovacaoRepository renovacaoRepository,
            ClienteRepository clienteRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.renovacaoRepository = renovacaoRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public RenovacaoResponseDTO criarRenovacao(
            String clienteId,
            RenovacaoRequestDTO dto
    ) {
        Cliente cliente = buscarClienteDoUsuario(clienteId);

        Renovacao renovacao = Renovacao.builder()
                .cliente(cliente)
                .dataInicio(dto.getDataInicio())
                .duracaoQuantidade(dto.getDuracaoQuantidade())
                .duracaoUnidade(dto.getDuracaoUnidade())
                .valor(dto.getValor())
                .observacao(dto.getObservacao())
                .build();

        Renovacao salva = renovacaoRepository.save(renovacao);
        return RenovacaoResponseDTO.fromEntity(salva);
    }

    public List<RenovacaoResponseDTO> listarRenovacoesPorCliente(String clienteId) {
        buscarClienteDoUsuario(clienteId);

        return renovacaoRepository.findByClienteId(clienteId)
                .stream()
                .map(RenovacaoResponseDTO::fromEntity)
                .toList();
    }

    public RenovacaoResponseDTO buscarRenovacaoPorId(
            String clienteId,
            String renovacaoId
    ) {
        buscarClienteDoUsuario(clienteId);
        Renovacao renovacao = buscarRenovacaoDoCliente(clienteId, renovacaoId);

        return RenovacaoResponseDTO.fromEntity(renovacao);
    }

    public RenovacaoResponseDTO atualizarRenovacao(
            String clienteId,
            String renovacaoId,
            RenovacaoRequestDTO dto
    ) {
        buscarClienteDoUsuario(clienteId);
        Renovacao renovacao = buscarRenovacaoDoCliente(clienteId, renovacaoId);

        renovacao.setDataInicio(dto.getDataInicio());
        renovacao.setDuracaoQuantidade(dto.getDuracaoQuantidade());
        renovacao.setDuracaoUnidade(dto.getDuracaoUnidade());
        renovacao.setValor(dto.getValor());
        renovacao.setObservacao(dto.getObservacao());
        renovacao.setDataVencimento(renovacao.calcularVencimento());

        Renovacao atualizada = renovacaoRepository.save(renovacao);
        return RenovacaoResponseDTO.fromEntity(atualizada);
    }

    public void deletarRenovacao(String clienteId, String renovacaoId) {
        buscarClienteDoUsuario(clienteId);
        Renovacao renovacao = buscarRenovacaoDoCliente(clienteId, renovacaoId);

        renovacaoRepository.delete(renovacao);
    }

    public List<RenovacaoResponseDTO> listarTodasRenovacoes() {
        String usuarioId = obterUsuarioAutenticado().getId();

        return renovacaoRepository.listarTodasComClientePorUsuario(usuarioId)
                .stream()
                .map(RenovacaoResponseDTO::fromEntity)
                .toList();
    }

    private Cliente buscarClienteDoUsuario(String clienteId) {
        String usuarioId = obterUsuarioAutenticado().getId();

        return clienteRepository.findByIdAndUsuario_Id(clienteId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente não encontrado com ID: " + clienteId
                ));
    }

    private Renovacao buscarRenovacaoDoCliente(
            String clienteId,
            String renovacaoId
    ) {
        return renovacaoRepository.findByIdAndClienteId(renovacaoId, clienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Renovação não encontrada com ID: " + renovacaoId
                                + " para o cliente " + clienteId
                ));
    }

    private Usuario obterUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário autenticado não encontrado."
                ));
    }
}