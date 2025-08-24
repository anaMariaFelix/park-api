package com.anamaria.park_api;

import com.anamaria.park_api.jwt.JwtToken;
import com.anamaria.park_api.web.controller.dto.UsuarioLoginDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.function.Consumer;

public class JwtAuthentication {

    //para gerar o token e conseguir fazer os testes
    public static Consumer<HttpHeaders> getHeaderAutorization(WebTestClient client, String username, String password){
        String token = client
                .post()
                .uri("/api/v1/auth")
                .bodyValue(new UsuarioLoginDTO(username,password))
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtToken.class)
                .returnResult().getResponseBody().getToken();

        return headers -> headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
}
