package com.anamaria.park_api.web.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class VagaResponseDTO {

    private Long id;
    private String codigo;
    private String status;
}
