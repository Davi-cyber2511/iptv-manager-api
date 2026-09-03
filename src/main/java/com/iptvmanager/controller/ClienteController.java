package com.iptvmanager.controller;

import com.iptvmanager.dto.ClienteRequestDTO;
import com.iptvmanager.dto.ClienteResponseDTO;
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

    // Novos endpoints para buscar por status
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
}
