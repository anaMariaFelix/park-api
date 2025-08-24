package com.anamaria.park_api.web.controller.dto.mapper;

import com.anamaria.park_api.entity.Vaga;
import com.anamaria.park_api.web.controller.dto.VagaCreateDTO;
import com.anamaria.park_api.web.controller.dto.VagaResponseDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VagaMapper {

    public static Vaga toVaga(VagaCreateDTO createDTO){
        return new ModelMapper().map(createDTO, Vaga.class);
    }

    public static VagaResponseDTO toVagaDTO(Vaga vaga){
        return new ModelMapper().map(vaga, VagaResponseDTO.class);
    }
}
