package com.anamaria.park_api.web.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class VagaCreateDTO {

    @NotBlank
    @Size(min = 4, max = 4)
    private String codigo;

    @NotBlank
    @Pattern(regexp = "LIVRE|OCUPADA") //SO ACEITA UM DESSAS STRINGS QUALQUER COISA DIFERENTE DISSO DARA ERRO
    private String status;
}
