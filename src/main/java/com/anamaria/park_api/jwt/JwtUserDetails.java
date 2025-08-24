package com.anamaria.park_api.jwt;

import com.anamaria.park_api.entity.Usuario;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class JwtUserDetails extends User {

    private Usuario usuario;

    public JwtUserDetails(Usuario usuario) {                                        //passaria os tipos de usuarios
        super(usuario.getUserName(), usuario.getPassword(), AuthorityUtils.createAuthorityList(usuario.getRole().name()));
        this.usuario = usuario;
    }

    //so precisa pegar o id e a role, pq como a classe extends da User ela ja tras o getuserName e o getpassword.
    public Long getId(){
        return this.usuario.getId();
    }

    public String getRole(){
        return this.usuario.getRole().name();
    }

}
