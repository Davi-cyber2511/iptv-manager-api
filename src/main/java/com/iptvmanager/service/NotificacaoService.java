package com.iptvmanager.service;

import com.iptvmanager.domain.Cliente;
import com.iptvmanager.domain.Notificacao;
import com.iptvmanager.domain.Renovacao;
import com.iptvmanager.domain.enums.CanalNotificacao;
import com.iptvmanager.domain.enums.StatusNotificacao;
import com.iptvmanager.domain.enums.TipoNotificacao;
import com.iptvmanager.repository.NotificacaoRepository;
import com.iptvmanager.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import com.iptvmanager.dto.NotificacaoResponseDTO;
import com.iptvmanager.domain.Usuario;
import com.iptvmanager.exception.ResourceNotFoundException;
import com.iptvmanager.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;


@Service
public class NotificacaoService {

    @Autowired
    private NotificacaoRepository notificacaoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    private UsuarioRepository usuarioRepository;

    @Transactional
    public void agendarNotificacoesVencimento(Cliente cliente, int diasAntesDoVencimento) {
        Optional<Renovacao> ultimaRenovacaoOpt = cliente.getUltimaRenovacao();

        if (ultimaRenovacaoOpt.isEmpty()) {
            System.out.println("Cliente " + cliente.getNome() + " não possui renovações. Nenhuma notificação agendada.");
            return; // Não há renovação para notificar
        }

        Renovacao ultimaRenovacao = ultimaRenovacaoOpt.get();
        LocalDate dataVencimento = ultimaRenovacao.getDataVencimento();

        // Calcula a data real em que a notificação deve ser enviada
        LocalDateTime dataEnvioProgramada = dataVencimento.minusDays(diasAntesDoVencimento).atStartOfDay();

        // Formata a mensagem base
        String mensagemBase = String.format(
                "Olá, %s! Seu serviço vence em %s. Por favor, contate seu revendedor para renovar.",
                cliente.getNome(),
                dataVencimento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );

        // Notificação por E-MAIL
        if (cliente.getEmail() != null && !cliente.getEmail().isEmpty()) {
            Notificacao notificacaoEmail = Notificacao.builder()
                    .cliente(cliente)
                    .tipoNotificacao(TipoNotificacao.PROXIMO_VENCIMENTO) // Usando o enum que você definiu
                    .canalNotificacao(CanalNotificacao.EMAIL)
                    .dataVencimentoReferencia(dataVencimento)
                    .dataEnvioProgramada(dataEnvioProgramada)
                    .status(StatusNotificacao.PENDENTE)
                    .mensagemEnviada(mensagemBase)
                    .build();
            notificacaoRepository.save(notificacaoEmail);
            System.out.println("Notificação de E-MAIL agendada para " + cliente.getEmail() + " em " + dataEnvioProgramada);
        }

        // Notificação por WHATSAPP
        if (cliente.getTelefone() != null && !cliente.getTelefone().isEmpty()) {
            Notificacao notificacaoWhatsapp = Notificacao.builder()
                    .cliente(cliente)
                    .tipoNotificacao(TipoNotificacao.PROXIMO_VENCIMENTO) // Usando o enum que você definiu
                    .canalNotificacao(CanalNotificacao.WHATSAPP)
                    .dataVencimentoReferencia(dataVencimento)
                    .dataEnvioProgramada(dataEnvioProgramada)
                    .status(StatusNotificacao.PENDENTE)
                    .mensagemEnviada(mensagemBase)
                    .build();
            notificacaoRepository.save(notificacaoWhatsapp);
            System.out.println("Notificação de WHATSAPP agendada para " + cliente.getTelefone() + " em " + dataEnvioProgramada);
        }
    }

    /**
     * Busca todas as notificações pendentes para um determinado canal,
     * cuja data de envio programada já chegou ou passou.
     */
    public List<Notificacao> buscarNotificacoesPendentesParaEnvio(CanalNotificacao canal) {
        return notificacaoRepository.findByStatusAndCanalNotificacaoAndDataEnvioProgramadaBefore(
                StatusNotificacao.PENDENTE,
                canal,
                LocalDateTime.now()
        );
    }

    /**
     * Atualiza o status de uma notificação.
     */
    @Transactional
    public Notificacao atualizarStatusNotificacao(Notificacao notificacao, StatusNotificacao novoStatus, String detalhesErro) {
        notificacao.setStatus(novoStatus);
        notificacao.setDetalhesErro(detalhesErro);
        if (novoStatus == StatusNotificacao.ENVIADO) {
            notificacao.setDataEnvioRealizada(LocalDateTime.now()); // Registra a data real de envio
        }
        return notificacaoRepository.save(notificacao);    }

    @Transactional(readOnly = true)
    public List<NotificacaoResponseDTO> listarNotificacoes() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário autenticado não encontrado."
                ));

        return notificacaoRepository.findAllByUsuarioId(usuario.getId())
                .stream()
                .map(NotificacaoResponseDTO::fromEntity)
                .toList();
    }


}