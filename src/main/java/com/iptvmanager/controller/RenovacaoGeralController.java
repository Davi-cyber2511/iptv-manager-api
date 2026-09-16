package com.iptvmanager.controller;

import com.iptvmanager.dto.RenovacaoResponseDTO;
import com.iptvmanager.service.RenovacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/renovacoes")
public class RenovacaoGeralController {

    private final RenovacaoService renovacaoService;

    @Autowired
    public RenovacaoGeralController(RenovacaoService renovacaoService) {
        this.renovacaoService = renovacaoService;
    }

    @GetMapping
    public ResponseEntity<List<RenovacaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(renovacaoService.listarTodasRenovacoes());
    }
}