package com.anamaria.park_api.web.controller.dto.mapper;

import com.anamaria.park_api.entity.Cliente;
import com.anamaria.park_api.web.controller.dto.ClienteCreateDTO;
import com.anamaria.park_api.web.controller.dto.ClienteResponseDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClienteMapper {

    public static Cliente toCliente(ClienteCreateDTO clienteCreateDTO){

        return new ModelMapper().map(clienteCreateDTO, Cliente.class);
    }

    public static ClienteResponseDTO toClienteDTO(Cliente cliente){

        return new ModelMapper().map(cliente, ClienteResponseDTO.class);
    }
}
