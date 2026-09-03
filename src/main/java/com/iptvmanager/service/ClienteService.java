package com.iptvmanager.service;

import com.iptvmanager.domain.Cliente;
import com.iptvmanager.domain.enums.StatusCliente;
import com.iptvmanager.dto.ClienteRequestDTO;
import com.iptvmanager.dto.ClienteResponseDTO;
import com.iptvmanager.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate; // Importe LocalDate
import java.util.List;
import java.util.stream.Collectors; // Importe Collectors

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
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        return ClienteResponseDTO.fromEntity(cliente);
    }

    // Novos métodos para buscar clientes por status
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
}
