package com.anamaria.park_api.web.controller.dto.mapper;

import com.anamaria.park_api.entity.Usuario;
import com.anamaria.park_api.web.controller.dto.UsuarioCreateDTO;
import com.anamaria.park_api.web.controller.dto.UsuarioResponseDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.util.List;
import java.util.stream.Collectors;

public class UsuarioMapper { //CLASSE USADA PRA TRABALHAR COM AS CONVENSOES DOS DTOs, USANDO O A DEPENDENCIA DOS MAPPER() QUE É RESPONSAVEL POR FAZER ESSAS CONVENÇÕES

    public static Usuario toUsuario(UsuarioCreateDTO usuarioDto){
        return new ModelMapper().map(usuarioDto, Usuario.class);
    }

    public static UsuarioResponseDTO toUsuarioDTO(Usuario usuario){
        String role = usuario.getRole().name().substring("ROLE_".length());

        PropertyMap<Usuario, UsuarioResponseDTO> props = new PropertyMap<Usuario, UsuarioResponseDTO>() {
            @Override
            protected void configure() {
                map().setRole(role);
            }
        };

        ModelMapper mapper = new ModelMapper();
        mapper.addMappings(props);
        return mapper.map(usuario, UsuarioResponseDTO.class);
    }

    public static List<UsuarioResponseDTO> toListUsuariosDTO(List<Usuario> usuarios){

        return usuarios.stream().map(u -> toUsuarioDTO(u)).collect(Collectors.toList());
    }
}
