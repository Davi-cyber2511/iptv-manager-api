package com.iptvmanager.domain;

import com.iptvmanager.domain.enums.StatusCliente;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String telefone;

    @Column(name = "servidor_iptv", nullable = false)
    private String servidorIptv;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(nullable = false)
    @Builder.Default
    private boolean ativo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Renovacao> renovacoes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Transient
    public Renovacao getRenovacaoAtual() {
        return renovacoes.stream()
                .max((r1, r2) -> r1.getDataVencimento().compareTo(r2.getDataVencimento()))
                .orElse(null);
    }

    @Transient
    public StatusCliente getStatus() {
        Renovacao atual = getRenovacaoAtual();
        if (atual == null) {
            return StatusCliente.VENCIDO;
        }

        LocalDate hoje = LocalDate.now();
        LocalDate vencimento = atual.getDataVencimento();

        return hoje.isAfter(vencimento)
                ? StatusCliente.VENCIDO
                : StatusCliente.ATIVO;
    }

}
