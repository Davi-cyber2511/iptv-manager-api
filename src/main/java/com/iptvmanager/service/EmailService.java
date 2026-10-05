package com.iptvmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    public void enviarEmail(String destinatario, String assunto, String corpo) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(remetente);
            message.setTo(destinatario);
            message.setSubject(assunto);
            message.setText(corpo);
            mailSender.send(message);
            System.out.println("Email enviado com sucesso para: " + destinatario);
        } catch (MailException e) {
            System.err.println("Erro ao enviar email para " + destinatario + ": " + e.getMessage());
            throw new RuntimeException("Falha ao enviar email", e); // Lança exceção para o scheduler tratar
        }
    }
}