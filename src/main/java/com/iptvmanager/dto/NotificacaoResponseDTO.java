package com.iptvmanager.dto;

import com.iptvmanager.domain.Notificacao;
import com.iptvmanager.domain.enums.CanalNotificacao;
import com.iptvmanager.domain.enums.StatusNotificacao;
import com.iptvmanager.domain.enums.TipoNotificacao;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NotificacaoResponseDTO {

    private String id;
    private String clienteId;
    private String clienteNome;
    private TipoNotificacao tipoNotificacao;
    private CanalNotificacao canalNotificacao;
    private LocalDate dataVencimentoReferencia;
    private LocalDateTime dataEnvioProgramada;
    private LocalDateTime dataEnvioRealizada;
    private StatusNotificacao status;
    private String mensagemEnviada;
    private String detalhesErro;

    public static NotificacaoResponseDTO fromEntity(Notificacao notificacao) {
        return NotificacaoResponseDTO.builder()
                .id(notificacao.getId())
                .clienteId(notificacao.getCliente() != null
                        ? notificacao.getCliente().getId()
                        : null)
                .clienteNome(notificacao.getCliente() != null
                        ? notificacao.getCliente().getNome()
                        : null)
                .tipoNotificacao(notificacao.getTipoNotificacao())
                .canalNotificacao(notificacao.getCanalNotificacao())
                .dataVencimentoReferencia(notificacao.getDataVencimentoReferencia())
                .dataEnvioProgramada(notificacao.getDataEnvioProgramada())
                .dataEnvioRealizada(notificacao.getDataEnvioRealizada())
                .status(notificacao.getStatus())
                .mensagemEnviada(notificacao.getMensagemEnviada())
                .detalhesErro(notificacao.getDetalhesErro())
                .build();
    }
}