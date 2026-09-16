package com.iptvmanager.controller;

import com.iptvmanager.dto.ClienteAtrasoDTO;
import com.iptvmanager.dto.DashboardResumoDTO;
import com.iptvmanager.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/resumo")
    public ResponseEntity<DashboardResumoDTO> getDashboardResumo() {
        DashboardResumoDTO resumo = dashboardService.getDashboardResumo();
        return ResponseEntity.ok(resumo);
    }

    @GetMapping("/clientes-atraso-prolongado")
    public ResponseEntity<List<ClienteAtrasoDTO>> getClientesAtrasoProlongado() {
        List<ClienteAtrasoDTO> clientes = dashboardService.getClientesAtrasoProlongado();
        return ResponseEntity.ok(clientes);
    }
}