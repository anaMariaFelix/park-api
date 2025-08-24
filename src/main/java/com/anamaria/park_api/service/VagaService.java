package com.anamaria.park_api.service;

import com.anamaria.park_api.entity.Vaga;
import com.anamaria.park_api.exception.CodigoUniqueViolationException;
import com.anamaria.park_api.exception.EntityNotFoundException;
import com.anamaria.park_api.repository.VagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.anamaria.park_api.entity.Vaga.StatusVaga.LIVRE;

@RequiredArgsConstructor
@Service
public class VagaService {

    private final VagaRepository vagaRepository;

    @Transactional
    public Vaga salvar(Vaga vaga) {
        try {
            return vagaRepository.save(vaga);
        }catch (DataIntegrityViolationException e){
            throw new CodigoUniqueViolationException(String.format("Vaga com o codigo '%s' já cadastrada", vaga.getCodigo()));
        }
    }

    @Transactional(readOnly = true)
    public Vaga buscarPorCodigo(String codigo){
        return vagaRepository.findByCodigo(codigo).orElseThrow(
                () -> new EntityNotFoundException(String.format("Vaga com o codigo '%s' não foi encontrada.", codigo))
        );
    }

    @Transactional(readOnly = true)
    public Vaga buscarPorVagaLivre() {
        return vagaRepository.findFirstByStatus(LIVRE).orElseThrow(
                () -> new EntityNotFoundException("Nenhuma Vaga livre foi encontrada."));
    }
}