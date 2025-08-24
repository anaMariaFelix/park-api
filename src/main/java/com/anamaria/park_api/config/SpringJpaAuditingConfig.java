package com.anamaria.park_api.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing //usada para fazer auditoria
@Configuration          //classe para auditoria
public class SpringJpaAuditingConfig implements AuditorAware<String> {//string pq sera o nome do usuario que sera auvido no sistema de auditoria, poderia ser outra coisa como o Id, dai usaria long.

    @Override
    public Optional<String> getCurrentAuditor() {//metodo da interface que deve ser implementado

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); //consegue pegar o usuario q esta logado

        if(authentication != null && authentication.isAuthenticated()){ //verifica se existe um objeto de authenticação
            return Optional.of(authentication.getName());
        }
        return null;
    }

}
