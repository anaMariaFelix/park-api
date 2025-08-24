package com.anamaria.park_api.jwt;

import com.anamaria.park_api.entity.Usuario;
import com.anamaria.park_api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service                                //usada para localizar objetos no banco de dados
public class JwtUserDetailsService implements UserDetailsService {

    private  final UserService userService;

    @Override //vai buscar um usuario por username, se ele for encontrado ele é retornado no formato de um userDetails
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = userService.buscarPorUserName(username);
        log.info("Usuário encontrado: {}, senha no banco: {}", usuario.getUserName(), usuario.getPassword());
        return new JwtUserDetails(usuario);//passa o usuario q esta logado no sistema
    }

    //utilizado para gerar o token jwt, quando o cliente vai autenticar na aplicação
    public JwtToken getTokenAuthenticated(String usuername){
        Usuario.Role role = userService.buscarRolePorUserName(usuername);
        return JwtUtils.createToken(usuername, role.name().substring("Role_".length()));
    }
}
