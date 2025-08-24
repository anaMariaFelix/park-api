package com.anamaria.park_api;

import com.anamaria.park_api.web.controller.dto.VagaCreateDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/SQL/vagas/vaga-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //@Sql usada pra executar os scriptes criados, executionPhase defini quando esses scriptes devem ser usados
@Sql(scripts = "/SQL/vagas/vaga-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class VagaIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void criarVaga_ComDadosValidos_RetornaLocationComStatus201(){
        testClient
                .post()
                .uri("api/v1/vagas")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))
                .bodyValue(new VagaCreateDTO("A-05", "LIVRE"))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION);
    }

    @Test
    public void criarVaga_ComCodigoJaExistente_RetornaErrorMessageComStatus409() {//erro quando ja existe uma vaga com o codigo passado
        testClient
                .post()
                .uri("api/v1/vagas")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))
                .bodyValue(new VagaCreateDTO("A-04", "LIVRE"))//esse codigo ja existe
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("status").isEqualTo(409)
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo("/api/v1/vagas");
    }

    @Test
    public void criarVaga_ComDadosInvalidos_RetornaErrorMessageComStatus422() {//erro quando Dados Invalidos são passados
        testClient
                .post()
                .uri("api/v1/vagas")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))
                .bodyValue(new VagaCreateDTO("", ""))//campos vazios
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo(422)
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo("/api/v1/vagas");

        testClient
                .post()
                .uri("api/v1/vagas")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))
                .bodyValue(new VagaCreateDTO("A-152", "desocupado"))//compos com informações invalidas
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo(422)
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo("/api/v1/vagas");
    }

    @Test
    public void buscarVaga_ComCodigoExistente_RetornaVagaComStatus201(){
        testClient
                .get()
                .uri("api/v1/vagas/{codigo}", "A-01")
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("id").isEqualTo(10)
                .jsonPath("codigo").isEqualTo("A-01")
                .jsonPath("status").isEqualTo("LIVRE");
    }

    @Test
    public void buscarVaga_ComCodigoInexistente_RetornaErrorMessageComStatus404(){
        testClient
                .get()
                .uri("api/v1/vagas/{codigo}", "A-10")//não existe uma vaga com esse codigo
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo(404)
                .jsonPath("method").isEqualTo("GET")
                .jsonPath("path").isEqualTo("/api/v1/vagas/A-10");
    }

    @Test
    public void buscarVaga_ComUsuarioSemPermissaoDeAcesso_RetornarErrorMessageComStatus403() { //Usuario Sem Permissao De Acesso
        testClient
                .get()
                .uri("/api/v1/vagas/{codigo}", "A-01")
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "maria@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo(403)
                .jsonPath("method").isEqualTo("GET")
                .jsonPath("path").isEqualTo("/api/v1/vagas/A-01");
    }

    @Test
    public void criarVaga_ComUsuarioSemPermissaoDeAcesso_RetornarErrorMessageComStatus403() {//Usuario Sem Permissao De Acesso
        testClient
                .post()
                .uri("/api/v1/vagas")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAutorization(testClient, "maria@email.com", "123456"))
                .bodyValue(new VagaCreateDTO("A-05", "OCUPADA"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo(403)
                .jsonPath("method").isEqualTo("POST")
                .jsonPath("path").isEqualTo("/api/v1/vagas");
    }
}
