package com.iptvmanager.scheduler;

import com.iptvmanager.domain.Cliente;
import com.iptvmanager.domain.Usuario;
import com.iptvmanager.domain.enums.StatusCliente;
import com.iptvmanager.integration.EmailService;
import com.iptvmanager.integration.WhatsAppService;
import com.iptvmanager.repository.ClienteRepository;
import com.iptvmanager.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class NotificationScheduler {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository; // Para notificar revendedores
    private final EmailService emailService;
    private final WhatsAppService whatsAppService;

    @Autowired
    public NotificationScheduler(ClienteRepository clienteRepository, UsuarioRepository usuarioRepository,
                                 EmailService emailService, WhatsAppService whatsAppService) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
        this.whatsAppService = whatsAppService;
    }

    // Habilite o agendamento no seu método main da aplicação:
    // @SpringBootApplication
    // @EnableScheduling
    // public class IptvManagerApplication { ... }

    // Executa todo dia à meia-noite (0 0 0 * * *)
    @Scheduled(cron = "0 0 0 * * *")
    public void checkAndSendNotifications() {
        LocalDate hoje = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<Cliente> todosClientes = clienteRepository.findAll();

        for (Cliente cliente : todosClientes) {
            StatusCliente status = cliente.getStatus();
            LocalDate dataVencimento = cliente.getDataVencimentoUltimaRenovacao();
            String nomeCliente = cliente.getNome();
            String telefoneCliente = cliente.getTelefone(); // Telefone do cliente para WhatsApp

            // Lógica para notificar o cliente via WhatsApp
            String mensagemWhatsApp = null;
            if (status == StatusCliente.VENCENDO_HOJE) {
                mensagemWhatsApp = String.format("Olá %s, sua assinatura vence HOJE, %s. Por favor, renove para evitar interrupções.", nomeCliente, dataVencimento.format(formatter));
            } else if (status == StatusCliente.PROXIMO_VENCIMENTO) {
                mensagemWhatsApp = String.format("Olá %s, sua assinatura vence em breve, no dia %s. Não se esqueça de renovar!", nomeCliente, dataVencimento.format(formatter));
            } else if (status == StatusCliente.VENCIDO) {
                mensagemWhatsApp = String.format("Olá %s, sua assinatura VENCEU no dia %s. Por favor, regularize sua situação.", nomeCliente, dataVencimento.format(formatter));
            }

            if (mensagemWhatsApp != null && telefoneCliente != null && !telefoneCliente.isEmpty()) {
                whatsAppService.sendWhatsAppMessage(telefoneCliente, mensagemWhatsApp);
            }

            // Lógica para notificar o revendedor via E-mail
            // Supondo que cada cliente tenha um revendedor associado, ou que todos os revendedores recebam alertas
            // Por simplicidade, vamos notificar todos os usuários com a role "REVENDEDOR"
            if (status == StatusCliente.VENCENDO_HOJE || status == StatusCliente.VENCIDO || status == StatusCliente.PROXIMO_VENCIMENTO) {
                List<Usuario> revendedores = usuarioRepository.findAll().stream()
                        .filter(u -> "REVENDEDOR".equals(u.getRole()))
                        .toList();

                for (Usuario revendedor : revendedores) {
                    String assuntoEmail = String.format("Alerta de Cliente: %s - %s", nomeCliente, status.name());
                    String corpoEmail = String.format("Prezado(a) %s,\n\nO cliente %s (Telefone: %s) está com o status: %s.\nData de Vencimento: %s.\n\nAtenciosamente,\nEquipe IPTVManager",
                            revendedor.getNome(), nomeCliente, telefoneCliente, status.name(), dataVencimento != null ? dataVencimento.format(formatter) : "N/A");
                    emailService.sendEmail(revendedor.getEmail(), assuntoEmail, corpoEmail);
                }
            }
        }
    }
}