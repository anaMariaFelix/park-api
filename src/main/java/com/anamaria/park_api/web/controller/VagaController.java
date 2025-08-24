package com.anamaria.park_api.web.controller;

import com.anamaria.park_api.entity.Vaga;
import com.anamaria.park_api.service.VagaService;
import com.anamaria.park_api.web.controller.dto.ClienteResponseDTO;
import com.anamaria.park_api.web.controller.dto.VagaCreateDTO;
import com.anamaria.park_api.web.controller.dto.VagaResponseDTO;
import com.anamaria.park_api.web.controller.dto.mapper.VagaMapper;
import com.anamaria.park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/vagas")
public class VagaController {

    private final VagaService vagaService;

    @Operation(summary = "Criar uma nova vaga", description = "Rescursos para criar uma nova vaga. "+
            "Requisição exige uso de bearer token. Acesso restrito a role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL do recurso criado")),
                    @ApiResponse(responseCode = "409", description = "Vaga já cadastrada",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Recurso não processado por falta de dados ou dados invalidos",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de CLIENTE",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> create(@RequestBody @Valid VagaCreateDTO vagaCreateDTO){
        Vaga vaga = VagaMapper.toVaga(vagaCreateDTO);
        vagaService.salvar(vaga);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{codigo}")// recupera a uri de acesso que foi usada para criar a vaga 'api/v1/vagas'
                .buildAndExpand(vaga.getCodigo())//inclui o codigo da vaga na uri
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Localiza uma vaga", description = "Rescursos para retorna uma vaga pelo seu codigo. "+
            "Requisição exige uso de bearer token. Acesso restrito a role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = VagaResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Vaga não localizada",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de CLIENTE",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            })
    @GetMapping("/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VagaResponseDTO> getByCodigo(@PathVariable String codigo){
        Vaga vaga = vagaService.buscarPorCodigo(codigo);
        return ResponseEntity.ok(VagaMapper.toVagaDTO(vaga));
    }
}
