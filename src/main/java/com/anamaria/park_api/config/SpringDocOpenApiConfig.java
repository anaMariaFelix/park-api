package com.anamaria.park_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocOpenApiConfig { //CLASSE DE CONFIGURAÇÃO PARA USAR O SWAGGER

    @Bean
    public OpenAPI openAPI() {//CONFIGURAÇÕES DA TELA DO SWAGGER, CONTENDO AS PRINCIPAIS INFORMAÇÕES
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("security", securityScheme())) //chamada do metodo que esta implementado logo abaixo
                .info(new Info()
                        .title("REST API - Spring Park")
                        .description("API para gestão de estacionamento de veículos")
                        .version("v1")
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0"))
                        .contact(new Contact().name("Ana Maria").email("anafelix0909@gmail.com"))
                );
    }

    //esse metodo foi criado por que quando existe uma implementação de segurança e necessario passar o token de acesso nas requisições que são feitas pela documentações do swagger no navegador
    private SecurityScheme securityScheme(){ //esse metodo so foi feito depois da implementação da segurança, sem a segurança na aplicação n precisa dele
        return new SecurityScheme()
                .description("Insira um bearer token valido para proceguir")
                .type(SecurityScheme.Type.HTTP)
                .in(SecurityScheme.In.HEADER)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("security"); //aqui pode ser qualquer valor
    }
}
