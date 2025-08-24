package com.anamaria.park_api.web.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UsuarioLoginDTO {

    @NotBlank
    @Email(message = "Formato de email invalido!", regexp = "^[a-z0-9.+-]+@[a-z0-9.+-]+\\.[a-z]{2,}$")
    private String userName;

    @NotBlank
    @Size(min = 6, max = 6)
    private String password;
}
