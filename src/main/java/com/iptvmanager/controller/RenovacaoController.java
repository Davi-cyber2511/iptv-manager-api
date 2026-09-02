package com.iptvmanager.controller;

import com.iptvmanager.dto.RenovacaoRequestDTO;
import com.iptvmanager.dto.RenovacaoResponseDTO;
import com.iptvmanager.service.RenovacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/renovacoes")
public class RenovacaoController {

    private final RenovacaoService renovacaoService;

    @Autowired
    public RenovacaoController(RenovacaoService renovacaoService) {
        this.renovacaoService = renovacaoService;
    }

    /**
     * Cria uma nova renovação.
     * POST /api/renovacoes
     * @param dto Dados da renovação a ser criada.
     * @return RenovacaoResponseDTO da renovação criada.
     */
    @PostMapping
    public ResponseEntity<RenovacaoResponseDTO> criarRenovacao(@Valid @RequestBody RenovacaoRequestDTO dto) {
        RenovacaoResponseDTO novaRenovacao = renovacaoService.criarRenovacao(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaRenovacao);
    }

    /**
     * Lista todas as renovações.
     * GET /api/renovacoes
     * @return Uma lista de RenovacaoResponseDTO.
     */
    @GetMapping
    public ResponseEntity<List<RenovacaoResponseDTO>> listarTodasRenovacoes() {
        List<RenovacaoResponseDTO> renovacoes = renovacaoService.listarTodasRenovacoes();
        return ResponseEntity.ok(renovacoes);
    }

    /**
     * Busca uma renovação pelo ID.
     * GET /api/renovacoes/{id}
     * @param id O ID da renovação.
     * @return RenovacaoResponseDTO da renovação encontrada.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RenovacaoResponseDTO> buscarRenovacaoPorId(@PathVariable String id) {
        RenovacaoResponseDTO renovacao = renovacaoService.buscarRenovacaoPorId(id);
        return ResponseEntity.ok(renovacao);
    }

    /**
     * Atualiza uma renovação existente.
     * PUT /api/renovacoes/{id}
     * @param id O ID da renovação a ser atualizada.
     * @param dto Dados atualizados da renovação.
     * @return RenovacaoResponseDTO da renovação atualizada.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RenovacaoResponseDTO> atualizarRenovacao(@PathVariable String id, @Valid @RequestBody RenovacaoRequestDTO dto) {
        RenovacaoResponseDTO renovacaoAtualizada = renovacaoService.atualizarRenovacao(id, dto);
        return ResponseEntity.ok(renovacaoAtualizada);
    }

    /**
     * Deleta uma renovação pelo ID.
     * DELETE /api/renovacoes/{id}
     * @param id O ID da renovação a ser deletada.
     * @return Resposta vazia com status NO_CONTENT.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarRenovacao(@PathVariable String id) {
        renovacaoService.deletarRenovacao(id);
        return ResponseEntity.noContent().build();
    }

}
