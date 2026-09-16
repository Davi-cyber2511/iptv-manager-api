package com.iptvmanager.repository;

import com.iptvmanager.domain.Renovacao;
import org.springframework.data.jpa.repository.JpaRepository; // Ou CrudRepository
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RenovacaoRepository extends JpaRepository<Renovacao, String> { // Ou CrudRepository

    List<Renovacao> findByClienteId(String clienteId);

    @Query("SELECT r FROM Renovacao r JOIN FETCH r.cliente ORDER BY r.dataVencimento ASC")
    List<Renovacao> listarTodasComCliente();
}