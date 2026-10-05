package com.iptvmanager.repository;

import com.iptvmanager.domain.Renovacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RenovacaoRepository extends JpaRepository<Renovacao, String> {

    List<Renovacao> findByClienteId(String clienteId);

    Optional<Renovacao> findByIdAndClienteId(String renovacaoId, String clienteId);

    @Query("""
            SELECT r
            FROM Renovacao r
            JOIN FETCH r.cliente c
            WHERE c.usuario.id = :usuarioId
            ORDER BY r.dataVencimento ASC
            """)
    List<Renovacao> listarTodasComClientePorUsuario(
            @Param("usuarioId") String usuarioId
    );
}