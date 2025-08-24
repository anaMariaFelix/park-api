package com.anamaria.park_api;

import com.anamaria.park_api.web.controller.dto.UsuarioCreateDTO;
import com.anamaria.park_api.web.controller.dto.UsuarioResponseDTO;
import com.anamaria.park_api.web.controller.dto.UsuarioSenhaDTO;
import com.anamaria.park_api.web.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

                                                                // esse WebEnvironment.RANDOM_PORT faz conque o toncat seja executado em uma porta de forma randomica,
                                                                // usasse o toncat dessa forma, para não usaur o toncat da propria aplicação, mas sim o toncat do proprio ambiente dee testes que essa biblioteca sede
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/SQL/usuarios/usuarios-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //@Sql usada pra executar os scriptes criados, executionPhase defini quando esses scriptes devem ser usados
@Sql(scripts = "/SQL/usuarios/usuarios-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UsuarioIT { //teste de ponto a ponto, esse tipo de teste tbm é chamado de teste de integração

    @Autowired
    WebTestClient testClient;

    @Test
    public void createUsuario_ComUserNameEPasswordValidos_RetornaUsuarioCriadoComStatus201(){
        UsuarioResponseDTO usuarioResponseDTO = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDTO("test@email.com", "123456"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UsuarioResponseDTO.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(usuarioResponseDTO).isNotNull();
        org.assertj.core.api.Assertions.assertThat(usuarioResponseDTO.getId()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(usuarioResponseDTO.getUsername()).isEqualTo("test@email.com");
        org.assertj.core.api.Assertions.assertThat(usuarioResponseDTO.getRole()).isEqualTo("CLIENTE");
    }

    @Test
    public void createUsuario_ComUserNameInvalido_RetornaErrorMessage422(){ //testa todas as possibilidades desse erro 422, todas as formas que ele pode ser lançado
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDTO("", "123456")) //string vazia
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDTO("ana@email", "123456"))//email sem o .com
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDTO("ana@", "123456"))//email incompleto
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

    }

    @Test
    public void createUsuario_ComPasswordInvalido_RetornaErrorMessage422(){ //testa todas as possibilidades desse erro 422, todas as formas que ele pode ser lançado
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDTO("test@email.com", "")) //senha vazia
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDTO("test@email.com", "123"))//senha menor que 6
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDTO("test@email.com", "123456789"))//senha maior que 6
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

    }


    @Test
    public void createUsuario_ComUserNameRepetido_RetornaErroMessageComStatus409(){
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDTO("ana@email.com", "123456"))//nesse caso de teste preciso passar um email já existente no banco
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);

    }

    @Test
    public void buscarUsuario_ComIdExistente_RetornaUsuarioComStatus200(){
        UsuarioResponseDTO usuarioResponseDTO = testClient
                .get()
                .uri("/api/v1/usuarios/100")//passa o id na url
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))//usando devido a seguranca, passando o usuario de teste que existe no arquivo usuarios-insert.sql
                .exchange()
                .expectStatus().isOk()
                .expectBody(UsuarioResponseDTO.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(usuarioResponseDTO).isNotNull();
        org.assertj.core.api.Assertions.assertThat(usuarioResponseDTO.getId()).isEqualTo(100);
        org.assertj.core.api.Assertions.assertThat(usuarioResponseDTO.getUsername()).isEqualTo("ana@email.com");
        org.assertj.core.api.Assertions.assertThat(usuarioResponseDTO.getRole()).isEqualTo("ADMIN");

       usuarioResponseDTO = testClient
                .get()
                .uri("/api/v1/usuarios/101")//passa o id na url
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UsuarioResponseDTO.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(usuarioResponseDTO).isNotNull();
        org.assertj.core.api.Assertions.assertThat(usuarioResponseDTO.getId()).isEqualTo(101);
        org.assertj.core.api.Assertions.assertThat(usuarioResponseDTO.getUsername()).isEqualTo("maria@email.com");
        org.assertj.core.api.Assertions.assertThat(usuarioResponseDTO.getRole()).isEqualTo("CLIENTE");

        usuarioResponseDTO = testClient
                .get()
                .uri("/api/v1/usuarios/101")//passa o id na url
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "maria@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UsuarioResponseDTO.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(usuarioResponseDTO).isNotNull();
        org.assertj.core.api.Assertions.assertThat(usuarioResponseDTO.getId()).isEqualTo(101);
        org.assertj.core.api.Assertions.assertThat(usuarioResponseDTO.getUsername()).isEqualTo("maria@email.com");
        org.assertj.core.api.Assertions.assertThat(usuarioResponseDTO.getRole()).isEqualTo("CLIENTE");
    }

    @Test
    public void buscarUsuario_ComIdInexistente_RetornaErroMessageComStatus404(){
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/usuarios/120")//passa o id na url
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))//usuario authenticado
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void buscarUsuario_ComUsuarioClienteBuscandoOutroCliente_RetornaErroMessageComStatus403(){
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/usuarios/102")//passa o id na url
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "maria@email.com", "123456"))//usuario authenticado
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void editaSenha_ComDadosValidos_RetornaStatus204(){
        testClient
                .patch()
                .uri("/api/v1/usuarios/100")//passa o id na url
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))//usuario authenticado
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDTO("123456", "123456","123456"))
                .exchange()
                .expectStatus().isNoContent();

        //como o metodo possui um retorno Void n precisa valirdar o objeto

        testClient
                .patch()
                .uri("/api/v1/usuarios/101")//passa o id na url
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "maria@email.com", "123456"))//usuario authenticado
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDTO("123456", "123456","123456"))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void editaSenha_ComUsuariosDiferentes_RetornaStatus403(){ //quando tenta munda a sua proopria senha mas informando um id q n é o seu ou q n existe
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/usuarios/0")//id invalido
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))//usuario authenticado
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDTO("123456", "123457","123457"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);

        responseBody = testClient
                .patch()
                .uri("/api/v1/usuarios/0")//id invalido
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "maria@email.com", "123456"))//usuario authenticado
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDTO("123456", "123457","123457"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void editaSenha_ComDadosInvalidos_RetornaStatus422(){
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/usuarios/100")//id valido
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))//usuario authenticado
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDTO("", "",""))//senhas vazias
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .patch()
                .uri("/api/v1/usuarios/100")//id valido
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))//usuario authenticado
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDTO("1234", "1234","1234"))//senhas menores que 6
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .patch()
                .uri("/api/v1/usuarios/100")//id valido
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))//usuario authenticado
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDTO("1234568", "1234578","1234578"))//senhas maiores que 6
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }


    @Test
    public void editaSenha_ComSenhasInvalidos_RetornaStatus400(){
        ErrorMessage responseBody = testClient
                .patch()
                .uri("/api/v1/usuarios/100")//id valido
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))//usuario authenticado
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDTO("123456", "123478","123479"))//senha atual valida, mas novaSenha e confirmaSenha diferentes
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

        responseBody = testClient
                .patch()
                .uri("/api/v1/usuarios/100")//id valido
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))//usuario authenticado
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDTO("123478", "123456","123456"))//senha atual invalida, mas novaSenha e confirmaSenha iguais e validas
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void buscarTodosOsUsuarios_RetornaListaDeUsuariosComStatus200(){
       List<UsuarioResponseDTO> usuariosResponseDTO = testClient
                .get()
                .uri("/api/v1/usuarios")
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))//usuario authenticado
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UsuarioResponseDTO.class)//retorna uma lista
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(usuariosResponseDTO).isNotNull();
        org.assertj.core.api.Assertions.assertThat(usuariosResponseDTO.size()).isEqualTo(3);//verifica se a lista que ele retornou tem a mesma quantidade que o banco

    }

    @Test
    public void buscarTodosOsUsuarios_ComUsuarioSemPermição_RetornaStatus403(){
        testClient
                .get()
                .uri("/api/v1/usuarios")
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "maria@email.com", "123456"))//usuario authenticado
                .exchange()
                .expectStatus().isForbidden();
    }
}
