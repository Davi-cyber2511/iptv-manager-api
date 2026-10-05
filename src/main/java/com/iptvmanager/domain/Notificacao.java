package com.iptvmanager.domain;

import com.iptvmanager.domain.enums.CanalNotificacao;
import com.iptvmanager.domain.enums.StatusNotificacao;
import com.iptvmanager.domain.enums.TipoNotificacao;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    @Column(
            name = "id",
            updatable = false,
            nullable = false,
            columnDefinition = "VARCHAR(36)"
    )
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_notificacao", nullable = false)
    private TipoNotificacao tipoNotificacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal_notificacao", nullable = false)
    private CanalNotificacao canalNotificacao;

    @Column(name = "data_vencimento_referencia", nullable = false)
    private LocalDate dataVencimentoReferencia;

    // Renomeado para clareza: esta é a data que o envio está programado
    @Column(name = "data_envio_programada", nullable = true) // Alterado para nullable = true
    private LocalDateTime dataEnvioProgramada;

    // Novo campo para registrar quando a notificação foi realmente enviada
    @Column(name = "data_envio_realizada", nullable = true)
    private LocalDateTime dataEnvioRealizada;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusNotificacao status;

    @Column(columnDefinition = "TEXT")
    private String mensagemEnviada;

    @Column(columnDefinition = "TEXT")
    private String detalhesErro;

    // Removido o @PrePersist que atribuía LocalDateTime.now() a dataEnvio
    // A lógica de preenchimento será feita no NotificacaoService
}