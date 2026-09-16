package com.iptvmanager.service;

import com.iptvmanager.domain.Cliente;
import com.iptvmanager.domain.enums.StatusCliente;
import com.iptvmanager.dto.ClienteRequestDTO;
import com.iptvmanager.dto.ClienteResponseDTO;
import com.iptvmanager.dto.ClienteStatusResumoDTO;
import com.iptvmanager.dto.ClienteUpdateDTO;
import com.iptvmanager.exception.ResourceNotFoundException;
import com.iptvmanager.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime; // Importe LocalDateTime
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public ClienteResponseDTO cadastrar(ClienteRequestDTO dto) {
        Cliente cliente = Cliente.builder()
                .nome(dto.getNome())
                .telefone(dto.getTelefone())
                .servidorIptv(dto.getServidorIptv())
                .observacoes(dto.getObservacoes())
                .ativo(dto.getAtivo())
                .build();

        Cliente salvo = clienteRepository.save(cliente);

        return ClienteResponseDTO.fromEntity(salvo);
    }

    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(ClienteResponseDTO::fromEntity)
                .toList();
    }

    public ClienteResponseDTO buscarPorId(String id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id)); // Use ResourceNotFoundException

        return ClienteResponseDTO.fromEntity(cliente);
    }

    // Novo método para atualizar cliente
    public ClienteResponseDTO atualizarCliente(String id, ClienteUpdateDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));

        cliente.setNome(dto.getNome());
        cliente.setTelefone(dto.getTelefone());
        cliente.setServidorIptv(dto.getServidorIptv());
        cliente.setObservacoes(dto.getObservacoes());
        if (dto.getAtivo() != null) { // Atualiza 'ativo' apenas se fornecido no DTO
            cliente.setAtivo(dto.getAtivo());
        }
        cliente.setUpdatedAt(LocalDateTime.now()); // Atualiza o campo updatedAt

        Cliente atualizado = clienteRepository.save(cliente);
        return ClienteResponseDTO.fromEntity(atualizado);
    }

    // Novo método para deletar cliente
    public void deletarCliente(String id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));
        clienteRepository.delete(cliente);
    }

    public List<ClienteResponseDTO> buscarClientesVencidos() {
        return clienteRepository.findAll().stream()
                .filter(cliente -> cliente.getStatus() == StatusCliente.VENCIDO)
                .map(ClienteResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ClienteResponseDTO> buscarClientesVencendoHoje() {
        return clienteRepository.findAll().stream()
                .filter(cliente -> cliente.getStatus() == StatusCliente.VENCENDO_HOJE)
                .map(ClienteResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ClienteResponseDTO> buscarClientesProximoVencimento() {
        return clienteRepository.findAll().stream()
                .filter(cliente -> cliente.getStatus() == StatusCliente.PROXIMO_VENCIMENTO)
                .map(ClienteResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ClienteResponseDTO> buscarClientesAtivos() {
        return clienteRepository.findAll().stream()
                .filter(cliente -> cliente.getStatus() == StatusCliente.ATIVO)
                .map(ClienteResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public ClienteStatusResumoDTO getResumoStatusClientes() {
        Map<StatusCliente, Long> contagemPorStatus = clienteRepository.findAll().stream()
                .collect(Collectors.groupingBy(Cliente::getStatus, Collectors.counting()));

        return ClienteStatusResumoDTO.builder()
                .ativos(contagemPorStatus.getOrDefault(StatusCliente.ATIVO, 0L))
                .vencendoHoje(contagemPorStatus.getOrDefault(StatusCliente.VENCENDO_HOJE, 0L))
                .proximoVencimento(contagemPorStatus.getOrDefault(StatusCliente.PROXIMO_VENCIMENTO, 0L))
                .vencidos(contagemPorStatus.getOrDefault(StatusCliente.VENCIDO, 0L))
                .build();
    }
}
