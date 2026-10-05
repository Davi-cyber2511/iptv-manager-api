package com.iptvmanager.controller;

import com.iptvmanager.dto.NotificacaoResponseDTO;
import com.iptvmanager.service.NotificacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @GetMapping
    public ResponseEntity<List<NotificacaoResponseDTO>> listarNotificacoes() {
        return ResponseEntity.ok(notificacaoService.listarNotificacoes());
    }
}