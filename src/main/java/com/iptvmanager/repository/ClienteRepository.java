package com.iptvmanager.repository;

import com.iptvmanager.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, String> {

    List<Cliente> findAllByUsuario_Id(String usuarioId);

    Optional<Cliente> findByIdAndUsuario_Id(String id, String usuarioId);
}