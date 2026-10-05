package com.iptvmanager.service;

import org.springframework.stereotype.Service;

@Service
public class WhatsappService {

    public void enviarMensagem(String numeroTelefone, String mensagem) {
        // Remover caracteres não numéricos do telefone para garantir o formato correto para a API
        String numeroLimpo = numeroTelefone.replaceAll("[^0-9]", "");

        System.out.println("Simulação: Tentando enviar mensagem WhatsApp para " + numeroLimpo + ": " + mensagem);

        // Por enquanto, apenas simula o envio
        System.out.println("Simulação: Mensagem WhatsApp '" + mensagem + "' enviada para " + numeroLimpo);
    }
}