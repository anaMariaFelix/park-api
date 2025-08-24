package com.anamaria.park_api.web.controller;

import com.anamaria.park_api.jwt.JwtToken;
import com.anamaria.park_api.jwt.JwtUserDetailsService;
import com.anamaria.park_api.web.controller.dto.UsuarioLoginDTO;
import com.anamaria.park_api.web.controller.dto.UsuarioResponseDTO;
import com.anamaria.park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticação", description = "Recurso para proceder com a autenticação na API") //documentação
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AutenticacaoController {

    private final JwtUserDetailsService userDetailsService;

    private final AuthenticationManager authenticationManager;


    //summary e oq esse recurso post faz
    @Operation(summary = "Autenticar na api", description = "Rescursos de autenticação na api",
            responses = { //responses informações sobre a resposta do recurso post
                    //codigo de resposta do metodo, tanto se de tudo certo quanto se ele não passar nas validações e lançar as exceptions
                    @ApiResponse(responseCode = "200", description = "Autenticação realizada com sucesso e retorno de um Bearer token",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),//schema é o objeto de retorno do recurso que nesse caso é um UsuarioResponseDTO
                    @ApiResponse(responseCode = "400", description = "Credencias invalidas",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Campos invalidos",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))//schema é o objeto de retorno do recurso que nesse caso é um ErrorMessage
            })
    @PostMapping("/auth")   //METODO USADO QUANDO O USUARIO FIZER UMA REQUISIÇÃO DE AUTENTICAÇÃO, PASSANDO O USERNAME E O PASSWORD
    public ResponseEntity<?> autenticar(@RequestBody @Valid UsuarioLoginDTO dto, HttpServletRequest request){

       log.info("Processo de autenticação pelo login {}",dto.getUserName());

       try{
           //OS VALORES DO USUARIO SERAM RECUPERADOS E PASSADOS PARA ESSA CLASSE USERNAMEPASSWORDAUTHENTICATIONTOKEN QUE VAI PEGAR ESSE USUARIO E SENHA E VAI BUSCAR NO BANCO
           UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(dto.getUserName(), dto.getPassword());
           log.info("2 log");
           authenticationManager.authenticate(authenticationToken);
           log.info("3 log");
           JwtToken token = userDetailsService.getTokenAuthenticated(dto.getUserName());
           log.info("4 log");
           return ResponseEntity.ok(token);

       }catch(AuthenticationException e){
            log.error("Bad Credentials from userName '{}'",dto.getUserName());
       }
       return ResponseEntity.badRequest().body(new ErrorMessage(request, HttpStatus.BAD_REQUEST,"Credenciais invalidas"));
    }

}
