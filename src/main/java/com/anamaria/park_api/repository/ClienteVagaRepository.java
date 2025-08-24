package com.anamaria.park_api.repository;

import com.anamaria.park_api.entity.Cliente;
import com.anamaria.park_api.entity.ClienteVaga;
import com.anamaria.park_api.repository.Projection.ClienteVagaProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteVagaRepository extends JpaRepository<ClienteVaga, Long> {

    Optional<ClienteVaga> findByReciboAndDataSaidaIsNull(String recibo);

    long countByClienteCpfAndDataSaidaIsNotNull(String cpf);

    Page<ClienteVagaProjection> findAllByClienteCpf(String cpf, Pageable pageable);

    Page<ClienteVagaProjection> findAllByClienteUsuarioId(Long id, Pageable pageable);
}
