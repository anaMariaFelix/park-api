package com.anamaria.park_api.web.controller.dto.mapper;

import com.anamaria.park_api.entity.ClienteVaga;
import com.anamaria.park_api.web.controller.dto.EstacionamentoCreateDTO;
import com.anamaria.park_api.web.controller.dto.EstacionamentoResponseDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import javax.swing.plaf.PanelUI;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClienteVagaMapper {

    public static ClienteVaga toClienteVaga(EstacionamentoCreateDTO dto){
        return  new ModelMapper().map(dto, ClienteVaga.class);
    }

    public static EstacionamentoResponseDTO toClienteVagaDTO(ClienteVaga clienteVaga){
        return  new ModelMapper().map(clienteVaga, EstacionamentoResponseDTO.class);
    }
}
