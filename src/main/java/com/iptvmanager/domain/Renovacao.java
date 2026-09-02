package com.iptvmanager.domain;

import com.iptvmanager.domain.enums.UnidadeDuracao;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "renovacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Renovacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "duracao_quantidade", nullable = false)
    private Integer duracaoQuantidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "duracao_unidade", nullable = false)
    private UnidadeDuracao duracaoUnidade;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.dataVencimento == null) {
            this.dataVencimento = calcularVencimento();
        }
    }

    public LocalDate calcularVencimento() {
        return switch (duracaoUnidade) {
            case DIAS -> dataInicio.plusDays(duracaoQuantidade);
            case MESES -> dataInicio.plusMonths(duracaoQuantidade);
            case ANOS -> dataInicio.plusYears(duracaoQuantidade);
        };
    }

}
