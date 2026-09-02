package com.iptvmanager.service;

import com.iptvmanager.domain.Cliente;
import com.iptvmanager.domain.Renovacao;
import com.iptvmanager.dto.RenovacaoRequestDTO;
import com.iptvmanager.dto.RenovacaoResponseDTO; // Precisaremos criar este DTO
import com.iptvmanager.repository.ClienteRepository;
import com.iptvmanager.repository.RenovacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    /**
     * Cria uma nova renovação para um cliente existente.
     * @param dto Dados da renovação a ser criada.
     * @return RenovacaoResponseDTO da renovação criada.
     */
    public RenovacaoResponseDTO criarRenovacao(RenovacaoRequestDTO dto) {
        // 1. Buscar o cliente pelo ID
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + dto.getClienteId()));

        // 2. Criar a entidade Renovacao a partir do DTO
        Renovacao renovacao = Renovacao.builder()
                .cliente(cliente) // Associa a renovação ao cliente encontrado
                .dataInicio(dto.getDataInicio())
                .duracaoQuantidade(dto.getDuracaoQuantidade())
                .duracaoUnidade(dto.getDuracaoUnidade())
                .valor(dto.getValor())
                .observacao(dto.getObservacao())
                .build();

        // 3. Calcular a data de vencimento (usando o método da entidade Renovacao)
        renovacao.setDataVencimento(renovacao.calcularVencimento());

        // 4. Adicionar a renovação à lista de renovações do cliente
        // É importante garantir que a lista de renovações do cliente não seja nula.
        // Se você usa Lombok @Builder.Default ou inicializa no construtor de Cliente,
        // essa verificação pode ser omitida.
        if (cliente.getRenovacoes() == null) {
            // cliente.setRenovacoes(new java.util.ArrayList<>()); // Descomente se necessário
        }
        cliente.getRenovacoes().add(renovacao);

        // 5. Salvar a renovação. Se o relacionamento tiver CascadeType.ALL/PERSIST,
        // salvar o cliente também salvaria a renovação.
        Renovacao renovacaoSalva = renovacaoRepository.save(renovacao);

        return RenovacaoResponseDTO.fromEntity(renovacaoSalva);
    }

    /**
     * Lista todas as renovações cadastradas.
     * @return Uma lista de RenovacaoResponseDTO.
     */
    public List<RenovacaoResponseDTO> listarTodasRenovacoes() {
        return renovacaoRepository.findAll().stream()
                .map(RenovacaoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Busca uma renovação pelo seu ID.
     * @param id O ID da renovação.
     * @return RenovacaoResponseDTO da renovação encontrada.
     */
    public RenovacaoResponseDTO buscarRenovacaoPorId(String id) {
        Renovacao renovacao = renovacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Renovação não encontrada com ID: " + id));
        return RenovacaoResponseDTO.fromEntity(renovacao);
    }

    /**
     * Atualiza uma renovação existente.
     * @param id O ID da renovação a ser atualizada.
     * @param dto Dados atualizados da renovação.
     * @return RenovacaoResponseDTO da renovação atualizada.
     */
    public RenovacaoResponseDTO atualizarRenovacao(String id, RenovacaoRequestDTO dto) {
        Renovacao renovacaoExistente = renovacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Renovação não encontrada com ID: " + id));

        // Atualiza os campos da renovação existente com os dados do DTO
        renovacaoExistente.setDataInicio(dto.getDataInicio());
        renovacaoExistente.setDuracaoQuantidade(dto.getDuracaoQuantidade());
        renovacaoExistente.setDuracaoUnidade(dto.getDuracaoUnidade());
        renovacaoExistente.setValor(dto.getValor());
        // Recalcula a data de vencimento se a duração ou data de início mudarem
        renovacaoExistente.setDataVencimento(renovacaoExistente.calcularVencimento());

        Renovacao renovacaoAtualizada = renovacaoRepository.save(renovacaoExistente);
        return RenovacaoResponseDTO.fromEntity(renovacaoAtualizada);
    }

    /**
     * Deleta uma renovação pelo seu ID.
     * @param id O ID da renovação a ser deletada.
     */
    public void deletarRenovacao(String id) {
        if (!renovacaoRepository.existsById(id)) {
            throw new RuntimeException("Renovação não encontrada com ID: " + id);
        }
        renovacaoRepository.deleteById(id);
    }
}
