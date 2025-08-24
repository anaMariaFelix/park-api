package com.anamaria.park_api.web.controller;

import com.anamaria.park_api.entity.Usuario;
import com.anamaria.park_api.service.UserService;
import com.anamaria.park_api.web.controller.dto.UsuarioCreateDTO;
import com.anamaria.park_api.web.controller.dto.UsuarioResponseDTO;
import com.anamaria.park_api.web.controller.dto.UsuarioSenhaDTO;
import com.anamaria.park_api.web.controller.dto.mapper.UsuarioMapper;
import com.anamaria.park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuarios",description = "Contem todas as operações relativas aos recursos para cadastro, adição e leitura de usuários.") //tag usada para documentação geral do controller no swagger
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/usuarios")
public class UsuarioController {

    private final UserService userService;

    //summary e oq esse recurso post faz
    @Operation(summary = "Criar um novo usuário", description = "Rescursos para criar um novo usuário",
            responses = { //responses informações sobre a resposta do recurso post
                //codigo de resposta do metodo, tanto se de tudo certo quanto se ele não passar nas validações e lançar as exceptions
                @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),//schema é o objeto de retorno do recurso que nesse caso é um UsuarioResponseDTO
                @ApiResponse(responseCode = "409", description = "Usuario email já cadastrado no sistema",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                @ApiResponse(responseCode = "422", description = "Recurso não processado por dados de entrada invalidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))//schema é o objeto de retorno do recurso que nesse caso é um ErrorMessage
            })
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioCreateDTO usuarioDTO){
        Usuario user = userService.salvar(UsuarioMapper.toUsuario(usuarioDTO));

        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toUsuarioDTO(user));
    }


    @Operation(summary = "Recupera um usuário pelo id", description = "Requisição exige um Bearer token, Acesso restrito a ADMIN|CLIENTE",
            security = @SecurityRequirement(name = "security"), //security é a mesma string que existe na classe de configuração do swagger, sem a seguraça n precisa adicionar essa linha
            responses = { //responses informações sobre a resposta do recurso post
                    //codigo de resposta do metodo, tanto se de tudo certo quanto se ele não passar nas validações e lançar as exceptions
                    @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),//schema é o objeto de retorno do recurso que nesse caso é um UsuarioResponseDTO
                    @ApiResponse(responseCode = "403", description = "Usuario sem permissão para acessar esse recurso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),//referente as permissoes de authenticação e acesso
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))//schema é o objeto de retorno do recurso que nesse caso é um ErrorMessage
            })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') OR (hasRole('CLIENTE')) AND #id == authentication.principal.id")//#id == authentication.principal.id significa que o cliente que esta logado so consegue buscar informações do usuario que o id passado seja igual ao id que esta no contexto do spring,
    public ResponseEntity<UsuarioResponseDTO> getById(@PathVariable Long id){                       //ou seja o id do proprio cliente, nesse caso ele so pode buscar as informações dele msm
        Usuario user = userService.buscarPorId(id);

        return ResponseEntity.ok(UsuarioMapper.toUsuarioDTO(user));
    }


    @Operation(summary = "Atualização de senhar", description = "Requisição exige um Bearer token, Acesso restrito a ADMIN|CLIENTE",
            security = @SecurityRequirement(name = "security"), //security é a mesma string que existe na classe de configuração do swagger, sem a seguraça n precisa adicionar essa linha
            responses = {
                    @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso"),//não precisa do content porque o retorno caso de certo é void
                    @ApiResponse(responseCode = "400", description = "Senha não confere",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Usuario sem permissão para acessar esse recurso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),//referente as permissoes de authenticação e acesso
                    @ApiResponse(responseCode = "422", description = "Campos invalidos ou mal formatados",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE') AND (#id == authentication.principal.id)") //hasAnyRole('ADMIN','CLIENTE') RECEBE UMA LISTA DE USUARIOS, E CADA USUARIO SO PODE AUTERAR SUA PROPRIA SENHA
    public ResponseEntity<Void> updatePassword(@PathVariable Long id,@Valid @RequestBody UsuarioSenhaDTO usuarioSenhaDTO){
        userService.editarSenha(id, usuarioSenhaDTO.getSenhaAtual(),usuarioSenhaDTO.getNovaSenha(),usuarioSenhaDTO.getConfirmaSenha());

        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Lista todos os Usuário cadastrados", description = "Requisição exige um Bearer token, Acesso restrito a ADMIN|CLIENTE",
            security = @SecurityRequirement(name = "security"), //security é a mesma string que existe na classe de configuração do swagger, sem a seguraça n precisa adicionar essa linha
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista com todos os Usuários cadastrados",
                            content = @Content(mediaType = "application/json",array = @ArraySchema(schema = @Schema(implementation = UsuarioResponseDTO.class)))),
                    @ApiResponse(responseCode = "403", description = "Usuario sem permissão para acessar esse recurso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class)))//referente as permissoes de authenticação e acesso
            })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponseDTO>> getAll(){
        List<Usuario> list = userService.buscarTodos();
        return ResponseEntity.ok(UsuarioMapper.toListUsuariosDTO(list));
    }
}
