package com.anamaria.park_api.config;

import com.anamaria.park_api.jwt.JwtAuthorizationFilter;
import com.anamaria.park_api.jwt.jwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableMethodSecurity
@EnableWebMvc //NECESSARIA PARA TRABALHAR COM SISTEMA DE SEGURAÇÃO
@Configuration
public class SpringSecurityConfig {

    private static final String[] DOCUMENTATION_OPENAPI = { //isso aqui é do swagger para que ele tenha acesso liberado msm com a aplicação tendo segurança
            "/docs/index.html",
            "/docs-park.html", "/docs-park/**",
            "/v3/api-docs/**",
            "/swagger-ui-custom.html", "/swagger-ui.html", "/swagger-ui/**",
            "/**.html", "/webjars/**", "/configuration/**", "/swagger-resources/**"
    };


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())//desabilitamdo essas funções para trabalhar com APIREST usando a forma stateless
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "api/v1/usuarios").permitAll()//essa ação de criação esta liberada para todos os usuarios ela é publica
                        .requestMatchers(HttpMethod.POST, "api/v1/auth").permitAll()//libera o acesso de forma publica para todos os usuarios que queiram realizar a autenticação na api
                        .requestMatchers(DOCUMENTATION_OPENAPI).permitAll() //do swagger
                        .anyRequest().authenticated()//isso defini que todas as outras requiçoes precisam de autenticação para serem utilizadas
                ).sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)//defini a politica de seção, como a aplicação é do tipo Ret a politica usada é a stateless
                ).addFilterBefore(
                        jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class //adiciona a ordem de execução dos filtros, primiero execulta o nosso filtro jwtAuthorizationFilter e so depois o UsernamePasswordAuthenticationFilter, se aordem for inversa não teria o resultado esperado
                ).exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new jwtAuthenticationEntryPoint())//instancia da classe que foi criada no pacakage jwt que lança uma exeção caso o usuario não esteja autenticado, dessa forma se o usuario n estiver logado a exeção sera lançada
                ).build();


    }

    @Bean //referente ao tipo de criptografia de senhas
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); //classe referente ao tipo de criptografia que sera utilizada q é a BCrypt atualmente ela é considerada a criptografia mais segura,
        // usando ela a cada vez q uma senha é criptografada é criada uma nova criptografia duferente  para a senha,
        // ou seja msm sendo a msm senha a criptofrafia dela sempre sera diferente a cada criptografia
    }

    @Bean //referente ao gerenciamento de autenticação
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); //retorna um objeto de gerenciamento de autenticação
    }

    @Bean //bean para colocar a classe filter sobre o gerenciamento do spring
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter();
    }

}