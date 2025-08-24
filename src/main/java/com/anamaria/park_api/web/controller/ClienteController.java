package com.anamaria.park_api.web.controller;

import com.anamaria.park_api.entity.Cliente;
import com.anamaria.park_api.jwt.JwtUserDetails;
import com.anamaria.park_api.repository.Projection.ClienteProjection;
import com.anamaria.park_api.service.ClienteService;
import com.anamaria.park_api.service.UserService;
import com.anamaria.park_api.web.controller.dto.ClienteCreateDTO;
import com.anamaria.park_api.web.controller.dto.ClienteResponseDTO;
import com.anamaria.park_api.web.controller.dto.PageableDTO;
import com.anamaria.park_api.web.controller.dto.mapper.ClienteMapper;
import com.anamaria.park_api.web.controller.dto.mapper.PageableMapper;
import com.anamaria.park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;


@Tag(name = "Clientes", description = "Contem todas as operações relativas ao recurso de um cliente")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final UserService userService;

    @Operation(summary = "Criar um novo cliente", description = "Rescursos para criar um novo cliente vinculado a um usuario cadastrado. "+
            "Requisição exige uso de bearer token. Acesso restrito a role='CLIENTE'",
            security = @SecurityRequirement(name = "security"),
            responses = { //responses informações sobre a resposta do recurso post
                    //codigo de resposta do metodo, tanto se de tudo certo quanto se ele não passar nas validações e lançar as exceptions
                    @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ClienteResponseDTO.class))),//schema é o objeto de retorno do recurso que nesse caso é um UsuarioResponseDTO
                    @ApiResponse(responseCode = "409", description = "Cliente CPF já possui cadastro no sistema",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ClienteResponseDTO.class))),
                    @ApiResponse(responseCode = "422", description = "Recurso não processado por falta de dados ou dados invalidos",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de ADMIN",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class)))//schema é o objeto de retorno do recurso que nesse caso é um ErrorMessage
            })
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")//apenas um cliente pode salvar um cliente
    public ResponseEntity<ClienteResponseDTO> create(@RequestBody @Valid ClienteCreateDTO clienteDto, @AuthenticationPrincipal JwtUserDetails userDetails){
        Cliente cliente = ClienteMapper.toCliente(clienteDto);
        cliente.setUsuario(userService.buscarPorId(userDetails.getId()));
        clienteService.salvar(cliente);

        return ResponseEntity.status(201).body(ClienteMapper.toClienteDTO(cliente));
    }

    @Operation(summary = "Localiza um cliente", description = "Rescursos para criar localizar um cliente pelo id. "+
            "Requisição exige uso de bearer token. Acesso restrito a role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ClienteResponseDTO.class))),//schema é o objeto de retorno do recurso que nesse caso é um UsuarioResponseDTO
                    @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ClienteResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de CLIENTE",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class)))//schema é o objeto de retorno do recurso que nesse caso é um ErrorMessage
            })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") //apenas admin pode buscar um cliente por id
    public ResponseEntity<ClienteResponseDTO> getById(@PathVariable Long id){
        Cliente cliente = clienteService.buscarPoId(id);
        return ResponseEntity.ok().body(ClienteMapper.toClienteDTO(cliente));
    }

    @Operation(summary = "Recuperar lista de clientes",
            description = "Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN' ",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = QUERY, name = "page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                            description = "Representa a página retornada"
                    ),
                    @Parameter(in = QUERY, name = "size",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "20")),
                            description = "Representa o total de elementos por página"
                    ),
                    @Parameter(in = QUERY, name = "sort", hidden = true, //hidden = true, usado para corrigir um erro do swagger ao trabalhar com type = string, escondendo da documentação
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "nome,asc")),
                            description = "Representa a ordenação dos resultados. Aceita multiplos critérios de ordenação são suportados.")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ClienteResponseDTO.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Recurso não permito ao perfil de CLIENTE",
                            content = @Content(mediaType = " application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            })
    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')") //apenas admin pode buscar um cliente por id
    public ResponseEntity<PageableDTO> getByAll(@Parameter(hidden = true) @PageableDefault(size = 5, sort ={"nome"}) Pageable pageable){ //Pageable usado para paginação, @Parameter(hidden = true) esconde a string do type = string da requisição
        Page<ClienteProjection> listCliente = clienteService.buscarTodosOsClientes(pageable); //@PageableDefault(size = 5, sort ={"nome"}) opcional, n ache que precise, ele ordena os objetos de acrdo com nome e cada pagina tem 5 objetos
        return ResponseEntity.ok(PageableMapper.toPageableDTO(listCliente));
    }

    @Operation(summary = "Recupera dados do cliente authenticado", description = "Requisição exige . "+
            "Requisição exige uso de um bearer token. Acesso restrito a Role='CLIENTE' ",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ClienteResponseDTO.class))),//schema é o objeto de retorno do recurso que nesse caso é um UsuarioResponseDTO
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de ADMIN",
                            content = @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorMessage.class)))//schema é o objeto de retorno do recurso que nesse caso é um ErrorMessage
            })
    @GetMapping("/detalhes")
    @PreAuthorize("hasRole('CLIENTE')") //apenas usuarios do tipo cliente pode buscar um cliente por detalhes, que retorna as informações do usuario logado
    public ResponseEntity<ClienteResponseDTO> getDetalhes(@AuthenticationPrincipal JwtUserDetails userDetails){ //@AuthenticationPrincipal JwtUserDetails pega o usuario que esta no constexto do spring
        Cliente cliente = clienteService.buscarPorUsuarioId(userDetails.getId());
        return ResponseEntity.ok(ClienteMapper.toClienteDTO(cliente));
    }
}
