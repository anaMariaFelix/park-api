package com.anamaria.park_api.repository;

import com.anamaria.park_api.entity.Cliente;
import com.anamaria.park_api.repository.Projection.ClienteProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("select c from Cliente c")
    Page<ClienteProjection> findAllPageable(Pageable pageable);

    Cliente findByUsuarioId(Long id);//metodo criado usando palavras chaves'findBy'

    Optional<Cliente> findByCpf(String cpf);
}
