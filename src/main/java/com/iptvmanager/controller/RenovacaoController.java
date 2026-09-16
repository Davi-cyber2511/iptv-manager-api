package com.iptvmanager.controller;

import com.iptvmanager.dto.RenovacaoRequestDTO;
import com.iptvmanager.dto.RenovacaoResponseDTO;
import com.iptvmanager.service.RenovacaoService;
import jakarta.validation.Valid; // Importe @Valid
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes/{clienteId}/renovacoes")
public class RenovacaoController {

    private final RenovacaoService renovacaoService;

    @Autowired
    public RenovacaoController(RenovacaoService renovacaoService) {
        this.renovacaoService = renovacaoService;
    }

    @PostMapping
    public ResponseEntity<RenovacaoResponseDTO> criarRenovacao(
            @PathVariable String clienteId,
            @RequestBody @Valid RenovacaoRequestDTO dto) { // Adicione @Valid
        RenovacaoResponseDTO renovacao = renovacaoService.criarRenovacao(clienteId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(renovacao);
    }

    @GetMapping
    public ResponseEntity<List<RenovacaoResponseDTO>> listarRenovacoesPorCliente(@PathVariable String clienteId) {
        List<RenovacaoResponseDTO> renovacoes = renovacaoService.listarRenovacoesPorCliente(clienteId);
        return ResponseEntity.ok(renovacoes);
    }

    @GetMapping("/{renovacaoId}")
    public ResponseEntity<RenovacaoResponseDTO> buscarRenovacaoPorId(
            @PathVariable String clienteId,
            @PathVariable String renovacaoId) {
        RenovacaoResponseDTO renovacao = renovacaoService.buscarRenovacaoPorId(clienteId, renovacaoId);
        return ResponseEntity.ok(renovacao);
    }

    @PutMapping("/{renovacaoId}")
    public ResponseEntity<RenovacaoResponseDTO> atualizarRenovacao(
            @PathVariable String clienteId,
            @PathVariable String renovacaoId,
            @RequestBody @Valid RenovacaoRequestDTO dto) { // Adicione @Valid
        RenovacaoResponseDTO renovacao = renovacaoService.atualizarRenovacao(clienteId, renovacaoId, dto);
        return ResponseEntity.ok(renovacao);
    }

    @DeleteMapping("/{renovacaoId}")
    public ResponseEntity<Void> deletarRenovacao(
            @PathVariable String clienteId,
            @PathVariable String renovacaoId) {
        renovacaoService.deletarRenovacao(clienteId, renovacaoId);
        return ResponseEntity.noContent().build();
    }
}