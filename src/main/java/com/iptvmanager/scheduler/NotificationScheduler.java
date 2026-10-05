package com.iptvmanager.scheduler;

import com.iptvmanager.domain.Cliente;
import com.iptvmanager.domain.Notificacao;
import com.iptvmanager.domain.enums.CanalNotificacao;
import com.iptvmanager.domain.enums.StatusNotificacao;
import com.iptvmanager.repository.ClienteRepository;
import com.iptvmanager.repository.UsuarioRepository;
import com.iptvmanager.service.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class NotificationScheduler {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacaoService notificacaoService;



    @Autowired
    public NotificationScheduler(
            ClienteRepository clienteRepository,
            UsuarioRepository usuarioRepository,
            NotificacaoService notificacaoService // Injetar o NotificacaoService

    ) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacaoService = notificacaoService;

    }

    @Scheduled(cron = "0 0 0 * * *")
    public void checkAndScheduleNotifications() { // Renomeado para refletir a nova função
        LocalDate hoje = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<Cliente> todosClientes = clienteRepository.findAll();

        for (Cliente cliente : todosClientes) {
            notificacaoService.agendarNotificacoesVencimento(cliente, 7);
        }
    }

    @Scheduled(fixedRate = 60000) // Executa a cada 1 minuto (60000 ms)
    public void sendPendingNotifications() {
        // Enviar notificações de E-MAIL
        List<Notificacao> emailPendentes = notificacaoService.buscarNotificacoesPendentesParaEnvio(CanalNotificacao.EMAIL);
        for (Notificacao notificacao : emailPendentes) {
            try {
                System.out.println("Simulando envio de E-MAIL para " + notificacao.getCliente().getEmail() + ": " + notificacao.getMensagemEnviada());
                notificacaoService.atualizarStatusNotificacao(notificacao, StatusNotificacao.ENVIADO, null);
            } catch (Exception e) {
                System.err.println("Falha ao enviar E-MAIL para " + notificacao.getCliente().getEmail() + ": " + e.getMessage());
                notificacaoService.atualizarStatusNotificacao(notificacao, StatusNotificacao.FALHA_ENVIO, e.getMessage());
            }
        }

        // Enviar notificações de WHATSAPP
        List<Notificacao> whatsappPendentes = notificacaoService.buscarNotificacoesPendentesParaEnvio(CanalNotificacao.WHATSAPP);
        for (Notificacao notificacao : whatsappPendentes) {            try {

                System.out.println("Simulando envio de WHATSAPP para " + notificacao.getCliente().getTelefone() + ": " + notificacao.getMensagemEnviada());
                notificacaoService.atualizarStatusNotificacao(notificacao, StatusNotificacao.ENVIADO, null);
            } catch (Exception e) {
                System.err.println("Falha ao enviar WHATSAPP para " + notificacao.getCliente().getTelefone() + ": " + e.getMessage());
                notificacaoService.atualizarStatusNotificacao(notificacao, StatusNotificacao.FALHA_ENVIO, e.getMessage());
            }
        }
    }
}