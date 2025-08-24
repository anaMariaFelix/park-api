package com.anamaria.park_api.service;

import com.anamaria.park_api.entity.Cliente;
import com.anamaria.park_api.exception.CpfUniqueViolationException;
import com.anamaria.park_api.exception.EntityNotFoundException;
import com.anamaria.park_api.jwt.JwtUserDetails;
import com.anamaria.park_api.repository.ClienteRepository;
import com.anamaria.park_api.repository.Projection.ClienteProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@RequiredArgsConstructor
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public Cliente salvar(Cliente cliente){
        try{
            return clienteRepository.save(cliente);
        }catch (DataIntegrityViolationException e){
            throw new CpfUniqueViolationException(String.format("Cpf %s não pode ser cadastrado, já existe no sistema",cliente.getCpf()));
        }
    }

    @Transactional(readOnly = true)
    public Cliente buscarPoId(Long id) {
        return clienteRepository.findById(id).orElseThrow(
                 () -> new EntityNotFoundException(String.format("Cliente com o id = %s não encontrado no sistema", + id))
        );
    }

    @Transactional(readOnly = true)
    public Page<ClienteProjection> buscarTodosOsClientes(Pageable pageable) {
        return clienteRepository.findAllPageable(pageable);
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorUsuarioId(Long id) {
        return clienteRepository.findByUsuarioId(id);
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf).orElseThrow(
                () -> new EntityNotFoundException(String.format("Clinte com CPF '%s' não encontrado", cpf))
        );
    }

}
