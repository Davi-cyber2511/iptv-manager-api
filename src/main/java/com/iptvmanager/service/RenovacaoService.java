package com.iptvmanager.service;

import com.iptvmanager.domain.Cliente;
import com.iptvmanager.domain.Renovacao;
import com.iptvmanager.dto.RenovacaoRequestDTO;
import com.iptvmanager.dto.RenovacaoResponseDTO;
import com.iptvmanager.exception.ResourceNotFoundException; // Importe a nova exceção
import com.iptvmanager.repository.ClienteRepository;
import com.iptvmanager.repository.RenovacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RenovacaoService {

    private final RenovacaoRepository renovacaoRepository;
    private final ClienteRepository clienteRepository;

    @Autowired
    public RenovacaoService(RenovacaoRepository renovacaoRepository, ClienteRepository clienteRepository) {
        this.renovacaoRepository = renovacaoRepository;
        this.clienteRepository = clienteRepository;
    }

    public RenovacaoResponseDTO criarRenovacao(String clienteId, RenovacaoRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + clienteId));

        Renovacao renovacao = Renovacao.builder()
                .cliente(cliente)
                .dataInicio(dto.getDataInicio())
                .duracaoQuantidade(dto.getDuracaoQuantidade())
                .duracaoUnidade(dto.getDuracaoUnidade())
                .valor(dto.getValor())
                .observacao(dto.getObservacao())
                .build();

        // O cálculo da data de vencimento já está no @PrePersist da entidade Renovacao
        // renovacao.setDataVencimento(renovacao.calcularVencimento());

        Renovacao salva = renovacaoRepository.save(renovacao);
        return RenovacaoResponseDTO.fromEntity(salva);
    }

    public List<RenovacaoResponseDTO> listarRenovacoesPorCliente(String clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + clienteId));

        return renovacaoRepository.findByClienteId(clienteId).stream()
                .map(RenovacaoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public RenovacaoResponseDTO buscarRenovacaoPorId(String clienteId, String renovacaoId) {
        // Verifica se o cliente existe
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + clienteId));

        // Busca a renovação e verifica se pertence ao cliente
        Renovacao renovacao = renovacaoRepository.findById(renovacaoId)
                .filter(r -> r.getCliente().getId().equals(clienteId))
                .orElseThrow(() -> new ResourceNotFoundException("Renovação não encontrada com ID: " + renovacaoId + " para o cliente " + clienteId));

        return RenovacaoResponseDTO.fromEntity(renovacao);
    }

    public RenovacaoResponseDTO atualizarRenovacao(String clienteId, String renovacaoId, RenovacaoRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + clienteId));

        Renovacao renovacao = renovacaoRepository.findById(renovacaoId)
                .filter(r -> r.getCliente().getId().equals(clienteId))
                .orElseThrow(() -> new ResourceNotFoundException("Renovação não encontrada com ID: " + renovacaoId + " para o cliente " + clienteId));

        renovacao.setDataInicio(dto.getDataInicio());
        renovacao.setDuracaoQuantidade(dto.getDuracaoQuantidade());
        renovacao.setDuracaoUnidade(dto.getDuracaoUnidade());
        renovacao.setValor(dto.getValor());
        renovacao.setObservacao(dto.getObservacao());

        // Recalcula a data de vencimento se os campos de duração mudaram
        renovacao.setDataVencimento(renovacao.calcularVencimento());

        Renovacao atualizada = renovacaoRepository.save(renovacao);
        return RenovacaoResponseDTO.fromEntity(atualizada);
    }

    public void deletarRenovacao(String clienteId, String renovacaoId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + clienteId));

        Renovacao renovacao = renovacaoRepository.findById(renovacaoId)
                .filter(r -> r.getCliente().getId().equals(clienteId))
                .orElseThrow(() -> new ResourceNotFoundException("Renovação não encontrada com ID: " + renovacaoId + " para o cliente " + clienteId));

        renovacaoRepository.delete(renovacao);
    }

    public List<RenovacaoResponseDTO> listarTodasRenovacoes() {
        return renovacaoRepository.listarTodasComCliente().stream()
                .map(RenovacaoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}