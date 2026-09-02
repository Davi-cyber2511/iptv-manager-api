package com.iptvmanager.service;

import com.iptvmanager.domain.Cliente;
// import com.iptvmanager.domain.Renovacao; // Não precisa mais importar Renovacao aqui
import com.iptvmanager.dto.ClienteRequestDTO;
import com.iptvmanager.dto.ClienteResponseDTO;
import com.iptvmanager.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public ClienteResponseDTO cadastrar(ClienteRequestDTO dto) {
        // A lógica de criação de Renovacao foi removida daqui
        // Renovacao renovacao = Renovacao.builder()
        //         .dataInicio(dto.getDataInicio())
        //         .duracaoQuantidade(dto.getDuracaoQuantidade())
        //         .duracaoUnidade(dto.getDuracaoUnidade())
        //         .valor(dto.getValor())
        //         .build();

        Cliente cliente = Cliente.builder()
                .nome(dto.getNome())
                .telefone(dto.getTelefone())
                .servidorIptv(dto.getServidorIptv())
                .observacoes(dto.getObservacoes())
                .ativo(dto.getAtivo()) // Adicionei o campo 'ativo' que está no seu DTO
                .build();

        // Se a lista de renovações do cliente for inicializada no construtor ou com @Builder.Default,
        // você não precisa fazer cliente.getRenovacoes().add(renovacao); aqui.
        // Se a lista puder ser nula, você pode inicializá-la:
        // if (cliente.getRenovacoes() == null) {
        //     cliente.setRenovacoes(new ArrayList<>());
        // }
        // renovacao.setCliente(cliente);
        // cliente.getRenovacoes().add(renovacao);


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

}
