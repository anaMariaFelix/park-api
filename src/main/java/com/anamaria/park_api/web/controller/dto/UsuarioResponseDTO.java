package com.anamaria.park_api.web.controller.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UsuarioResponseDTO {

    private Long id;
    private String Username;
    private String role;
}
