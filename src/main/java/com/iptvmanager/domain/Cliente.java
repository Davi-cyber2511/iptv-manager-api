package com.iptvmanager.domain;

import com.iptvmanager.domain.enums.StatusCliente;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clientes")
public class Cliente {

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

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String telefone;

    @Column(name = "servidor_iptv", nullable = false)
    private String servidorIptv;

    private String observacoes;

    @Column(nullable = false)
    private Boolean ativo;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(
            mappedBy = "cliente",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("dataInicio DESC")
    private List<Renovacao> renovacoes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        LocalDateTime agora = LocalDateTime.now();

        if (this.createdAt == null) {
            this.createdAt = agora;
        }

        if (this.updatedAt == null) {
            this.updatedAt = agora;
        }

        if (this.renovacoes == null) {
            this.renovacoes = new ArrayList<>();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Transient
    public Optional<Renovacao> getUltimaRenovacao() {
        if (renovacoes == null || renovacoes.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(renovacoes.get(0));
    }

    @Transient
    public StatusCliente getStatus() {
        if (Boolean.FALSE.equals(this.ativo)) {
            return StatusCliente.INATIVO;
        }

        Optional<Renovacao> ultimaRenovacaoOpt = getUltimaRenovacao();

        if (ultimaRenovacaoOpt.isEmpty()) {
            return StatusCliente.SEM_RENOVACAO;
        }

        Renovacao ultimaRenovacao = ultimaRenovacaoOpt.get();
        LocalDate dataVencimento = ultimaRenovacao.getDataVencimento();
        LocalDate hoje = LocalDate.now();

        if (dataVencimento == null) {
            return StatusCliente.PENDENTE;
        }

        if (dataVencimento.isBefore(hoje)) {
            return StatusCliente.VENCIDO;
        }

        if (dataVencimento.isEqual(hoje)) {
            return StatusCliente.VENCENDO_HOJE;
        }

        /*
         * Considera como próximo vencimento qualquer data entre amanhã
         * e exatamente sete dias a partir de hoje.
         */
        LocalDate limiteProximoVencimento = hoje.plusDays(7);

        if (!dataVencimento.isAfter(limiteProximoVencimento)) {
            return StatusCliente.PROXIMO_VENCIMENTO;
        }

        return StatusCliente.ATIVO;
    }

    @Transient
    public BigDecimal getValorUltimaRenovacao() {
        return getUltimaRenovacao()
                .map(Renovacao::getValor)
                .orElse(null);
    }

    @Transient
    public LocalDate getDataVencimentoUltimaRenovacao() {
        return getUltimaRenovacao()
                .map(Renovacao::getDataVencimento)
                .orElse(null);
    }
}