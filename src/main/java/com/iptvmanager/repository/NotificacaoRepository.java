package com.iptvmanager.repository;

import com.iptvmanager.domain.Notificacao;
import com.iptvmanager.domain.enums.CanalNotificacao;
import com.iptvmanager.domain.enums.StatusNotificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, String> {

    List<Notificacao> findByStatusAndCanalNotificacaoAndDataEnvioProgramadaBefore(
            StatusNotificacao status,
            CanalNotificacao canal,
            LocalDateTime now
    );

    @Query("""
            SELECT n
            FROM Notificacao n
            JOIN FETCH n.cliente c
            WHERE c.usuario.id = :usuarioId
            ORDER BY n.dataEnvioProgramada DESC
            """)
    List<Notificacao> findAllByUsuarioId(
            @Param("usuarioId") String usuarioId
    );
}