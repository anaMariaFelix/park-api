package com.anamaria.park_api;


import com.anamaria.park_api.web.controller.dto.ClienteCreateDTO;
import com.anamaria.park_api.web.controller.dto.ClienteResponseDTO;
import com.anamaria.park_api.web.controller.dto.PageableDTO;
import com.anamaria.park_api.web.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

// esse WebEnvironment.RANDOM_PORT faz conque o toncat seja executado em uma porta de forma randomica,
                                                // usasse o toncat dessa forma, para não usaur o toncat da propria aplicação, mas sim o toncat do proprio ambiente dee testes que essa biblioteca sede
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/SQL/clientes/cliente-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //@Sql usada pra executar os scriptes criados, executionPhase defini quando esses scriptes devem ser usados
@Sql(scripts = "/SQL/clientes/cliente-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ClienteIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void createCliente_ComDadosValidos_RetornaClienteComStatus201(){ //teste para criar um cliente valido
        ClienteResponseDTO clienteResponseDTO = testClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "wilker@email.com", "123456"))
                .bodyValue(new ClienteCreateDTO("wilker freitas", "71561593052"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ClienteResponseDTO.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(clienteResponseDTO).isNotNull();
        org.assertj.core.api.Assertions.assertThat(clienteResponseDTO.getId()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(clienteResponseDTO.getNome()).isEqualTo("wilker freitas");
        org.assertj.core.api.Assertions.assertThat(clienteResponseDTO.getCpf()).isEqualTo("71561593052");
    }

    @Test
    public void createCliente_ComCPFJaCadastrado_RetornaErroMessageStatus409() { //todos os dados validos menos o cpf
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "wilker@email.com", "123456"))
                .bodyValue(new ClienteCreateDTO("wilker freitas", "22401480048"))//cpf já cadastrado no banco, pertence ao sid no insert.sql por isso vai da erro
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);

    }

    @Test
    public void createCliente_ComDadosInvalidos_RetornaErroMessageStatus422() { //dados invalidos
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "wilker@email.com", "123456"))
                .bodyValue(new ClienteCreateDTO("", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "wilker@email.com", "123456"))
                .bodyValue(new ClienteCreateDTO("ana", "00000000000"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "wilker@email.com", "123456"))
                .bodyValue(new ClienteCreateDTO("ana", "715.615.930-52"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

    }

    @Test
    public void createCliente_ComUsuarioNaoPermitido_RetornaErroMessageStatus403() { //usuario sem permisão no caso de um admin
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))//ana é admin n tem permissao para criar clientes
                .bodyValue(new ClienteCreateDTO("wilker freitas", "22401480048"))
                .exchange()
                .expectStatus().isEqualTo(403)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);

    }

    @Test
    public void buscarCliente_ComIdExistentePeloAdmin_RetornaClienteComStatus200() { //fluxo feliz
        ClienteResponseDTO responseBody = testClient
                .get()
                .uri("/api/v1/clientes/10")
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))//ana é admin e tem permissao para buscar clientes pelo id
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClienteResponseDTO.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(10);

    }

    @Test
    public void buscarCliente_ComIdInexistentePeloAdmin_RetornaErroMessageStatus404() { //id invalido
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/clientes/15")//id inexistente
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))//ana é admin n tem permissao para criar clientes
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);

    }

    @Test
    public void buscarCliente_ComIdExistentePeloCliente_RetornaErroMessageStatus403() { //id valido, mas cliente não tem permissao
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/clientes/10")//id inexistente
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "maria@email.com", "123456"))//ana é admin n tem permissao para criar clientes
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);

    }

    @Test
    public void buscarCliente_ComPaginacaoPeloAdmin_RetornaClienteComStatus200() { //busca clientes com admin valido
        PageableDTO responseBody = testClient
                .get()
                .uri("/api/v1/clientes")//id inexistente
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))//ana é admin n tem permissao para criar clientes
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDTO.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(1);

        responseBody = testClient
                .get()
                .uri("/api/v1/clientes?size=1&page=1")//id inexistente
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))//ana é admin n tem permissao para criar clientes
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDTO.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void buscarCliente_ComPaginacaoPeloCliente_RetornaErrorMessageComStatus403() { //busca clientes com usuario do tipo CLIENTE que n tem acesso
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/clientes")//id inexistente
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "maria@email.com", "123456"))//ana é admin n tem permissao para criar clientes
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void buscarCliente_ComDadosDoTokenDeCliente_RetornaClienteComStatus200() { //busca clientes com usuario do tipo CLIENTE que n tem acesso
        ClienteResponseDTO responseBody = testClient
                .get()
                .uri("/api/v1/clientes/detalhes")//id inexistente
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "maria@email.com", "123456"))//ana é admin n tem permissao para criar clientes
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClienteResponseDTO.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getCpf()).isEqualTo("61056769050");
        org.assertj.core.api.Assertions.assertThat(responseBody.getNome()).isEqualTo("maria alves");
        org.assertj.core.api.Assertions.assertThat(responseBody.getId()).isEqualTo(10);
    }

    @Test
    public void buscarCliente_ComDadosDoTokenDeAdmin_RetornaErrorMessageComStatus403() { //busca clientes com usuario do tipo CLIENTE que n tem acesso
        ErrorMessage responseBody = testClient
                .get()
                .uri("/api/v1/clientes/detalhes")//id inexistente
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))//ana é admin n tem permissao para criar clientes
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }


}
