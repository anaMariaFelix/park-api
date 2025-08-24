package com.anamaria.park_api.repository;

import com.anamaria.park_api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUserName(String username);


    @Query("select u.role from Usuario u where u.userName like :username")//jpl
    Usuario.Role findRoleByUserName(String username);
}