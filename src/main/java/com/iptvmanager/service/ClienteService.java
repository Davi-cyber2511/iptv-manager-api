package com.iptvmanager.service;

import com.iptvmanager.domain.Cliente;
import com.iptvmanager.domain.Usuario;
import com.iptvmanager.domain.enums.StatusCliente;
import com.iptvmanager.dto.ClienteRequestDTO;
import com.iptvmanager.dto.ClienteResponseDTO;
import com.iptvmanager.dto.ClienteStatusResumoDTO;
import com.iptvmanager.dto.ClienteUpdateDTO;
import com.iptvmanager.exception.ResourceNotFoundException;
import com.iptvmanager.repository.ClienteRepository;
import com.iptvmanager.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    public ClienteService(
            ClienteRepository clienteRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public ClienteResponseDTO cadastrar(ClienteRequestDTO dto) {
        Usuario usuario = obterUsuarioAutenticado();

        Cliente cliente = Cliente.builder()
                .nome(dto.getNome())
                .telefone(dto.getTelefone())
                .email(dto.getEmail())
                .servidorIptv(dto.getServidorIptv())
                .observacoes(dto.getObservacoes())
                .ativo(dto.getAtivo())
                .usuario(usuario)
                .build();

        Cliente salvo = clienteRepository.save(cliente);

        return ClienteResponseDTO.fromEntity(salvo);
    }

    public List<ClienteResponseDTO> listarTodos() {
        String usuarioId = obterUsuarioAutenticado().getId();

        return clienteRepository.findAllByUsuario_Id(usuarioId)
                .stream()
                .map(ClienteResponseDTO::fromEntity)
                .toList();
    }

    public ClienteResponseDTO buscarPorId(String id) {
        Cliente cliente = buscarClienteDoUsuario(id);
        return ClienteResponseDTO.fromEntity(cliente);
    }

    public ClienteResponseDTO atualizarCliente(String id, ClienteUpdateDTO dto) {
        Cliente cliente = buscarClienteDoUsuario(id);

        cliente.setNome(dto.getNome());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEmail(dto.getEmail());
        cliente.setServidorIptv(dto.getServidorIptv());
        cliente.setObservacoes(dto.getObservacoes());

        if (dto.getAtivo() != null) {
            cliente.setAtivo(dto.getAtivo());
        }

        cliente.setUpdatedAt(LocalDateTime.now());

        Cliente atualizado = clienteRepository.save(cliente);
        return ClienteResponseDTO.fromEntity(atualizado);
    }

    public void deletarCliente(String id) {
        Cliente cliente = buscarClienteDoUsuario(id);
        clienteRepository.delete(cliente);
    }

    public List<ClienteResponseDTO> buscarClientesVencidos() {
        return listarClientesDoUsuario().stream()
                .filter(cliente -> cliente.getStatus() == StatusCliente.VENCIDO)
                .map(ClienteResponseDTO::fromEntity)
                .toList();
    }

    public List<ClienteResponseDTO> buscarClientesVencendoHoje() {
        return listarClientesDoUsuario().stream()
                .filter(cliente -> cliente.getStatus() == StatusCliente.VENCENDO_HOJE)
                .map(ClienteResponseDTO::fromEntity)
                .toList();
    }

    public List<ClienteResponseDTO> buscarClientesProximoVencimento() {
        return listarClientesDoUsuario().stream()
                .filter(cliente -> cliente.getStatus() == StatusCliente.PROXIMO_VENCIMENTO)
                .map(ClienteResponseDTO::fromEntity)
                .toList();
    }

    public List<ClienteResponseDTO> buscarClientesAtivos() {
        return listarClientesDoUsuario().stream()
                .filter(cliente -> cliente.getStatus() == StatusCliente.ATIVO)
                .map(ClienteResponseDTO::fromEntity)
                .toList();
    }

    public ClienteStatusResumoDTO getResumoStatusClientes() {
        Map<StatusCliente, Long> contagemPorStatus = listarClientesDoUsuario()
                .stream()
                .collect(Collectors.groupingBy(
                        Cliente::getStatus,
                        Collectors.counting()
                ));

        return ClienteStatusResumoDTO.builder()
                .ativos(contagemPorStatus.getOrDefault(StatusCliente.ATIVO, 0L))
                .vencendoHoje(contagemPorStatus.getOrDefault(StatusCliente.VENCENDO_HOJE, 0L))
                .proximoVencimento(contagemPorStatus.getOrDefault(StatusCliente.PROXIMO_VENCIMENTO, 0L))
                .vencidos(contagemPorStatus.getOrDefault(StatusCliente.VENCIDO, 0L))
                .build();
    }

    private List<Cliente> listarClientesDoUsuario() {
        String usuarioId = obterUsuarioAutenticado().getId();
        return clienteRepository.findAllByUsuario_Id(usuarioId);
    }

    private Cliente buscarClienteDoUsuario(String clienteId) {
        String usuarioId = obterUsuarioAutenticado().getId();

        return clienteRepository.findByIdAndUsuario_Id(clienteId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente não encontrado com ID: " + clienteId
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