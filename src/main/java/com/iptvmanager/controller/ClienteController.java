package com.iptvmanager.controller;

import com.iptvmanager.dto.ClienteRequestDTO;
import com.iptvmanager.dto.ClienteResponseDTO;
import com.iptvmanager.dto.ClienteStatusResumoDTO;
import com.iptvmanager.dto.ClienteUpdateDTO; // Importe o novo DTO
import com.iptvmanager.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> cadastrar(@RequestBody @Valid ClienteRequestDTO dto) {
        ClienteResponseDTO cliente = clienteService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarTodos() {
        List<ClienteResponseDTO> clientes = clienteService.listarTodos();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable String id) {
        ClienteResponseDTO cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(cliente);
    }

    // Novo endpoint para atualizar cliente
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(
            @PathVariable String id,
            @RequestBody @Valid ClienteUpdateDTO dto) { // Use @Valid com ClienteUpdateDTO
        ClienteResponseDTO cliente = clienteService.atualizarCliente(id, dto);
        return ResponseEntity.ok(cliente);
    }

    // Novo endpoint para deletar cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable String id) {
        clienteService.deletarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/vencidos")
    public ResponseEntity<List<ClienteResponseDTO>> getClientesVencidos() {
        List<ClienteResponseDTO> clientes = clienteService.buscarClientesVencidos();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/status/vencendo-hoje")
    public ResponseEntity<List<ClienteResponseDTO>> getClientesVencendoHoje() {
        List<ClienteResponseDTO> clientes = clienteService.buscarClientesVencendoHoje();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/status/proximo-vencimento")
    public ResponseEntity<List<ClienteResponseDTO>> getClientesProximoVencimento() {
        List<ClienteResponseDTO> clientes = clienteService.buscarClientesProximoVencimento();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/status/ativos")
    public ResponseEntity<List<ClienteResponseDTO>> getClientesAtivos() {
        List<ClienteResponseDTO> clientes = clienteService.buscarClientesAtivos();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/resumo-status")
    public ResponseEntity<ClienteStatusResumoDTO> getResumoStatusClientes() {
        ClienteStatusResumoDTO resumo = clienteService.getResumoStatusClientes();
        return ResponseEntity.ok(resumo);
    }
}
