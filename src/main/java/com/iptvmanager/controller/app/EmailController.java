package com.iptvmanager.controller.app;

import com.iptvmanager.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/enviar-teste")
    public ResponseEntity<String> enviarEmailDeTeste(
            @RequestParam String para,
            @RequestParam String assunto,
            @RequestParam String corpo) {

        emailService.enviarEmail(para, assunto, corpo);
        return ResponseEntity.ok("E-mail de teste enviado para " + para);
    }
}

