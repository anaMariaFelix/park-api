package com.anamaria.park_api.service;

import com.anamaria.park_api.exception.EntityNotFoundException;
import com.anamaria.park_api.exception.PasswordInvalidException;
import com.anamaria.park_api.exception.UserNameUniqueViolationException;
import com.anamaria.park_api.entity.Usuario;
import com.anamaria.park_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
public class UserService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; //injeção do @bean que foi criado na classe de configuração.

    @Transactional
    public Usuario salvar(Usuario usuario) {
        try{
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));//fazendo isso o passwordEncoder.encode realiza a criptografia da senha retornando a senha criptografada
            return usuarioRepository.save(usuario);
        }catch (DataIntegrityViolationException e){
            throw new UserNameUniqueViolationException(String.format("UserName {%s} já cadastrado", usuario.getUserName()));
        }

    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuario id = %s não encontrado", id))
        );
    }

    @Transactional
    public Usuario editarSenha(Long id, String senhaAtual, String novaSenha, String confirmaSenha) {

        if(!novaSenha.equals(confirmaSenha)){
            throw new PasswordInvalidException("Nova senha não confere com a confirmação de senha");
        }
        Usuario user = buscarPorId(id);
        //if(!user.getPassword().equals(senhaAtual)) sem criptografia
        if(!passwordEncoder.matches(senhaAtual, user.getPassword())){//com criptografia, passa senha atual mandada na requisição e compara usando o matches com a senha criptogrfada do user.getPassword
            throw new PasswordInvalidException("Sua senha não confere.");
        }

      //user.setPassword(novaSenha);
        user.setPassword(passwordEncoder.encode(novaSenha));
        return user;
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarTodos() {
       return usuarioRepository.findAll();
    }


    @Transactional(readOnly = true)
    public Usuario buscarPorUserName(String username) {
        return usuarioRepository.findByUserName(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuario com userName = %s não encontrado", username)));
    }

    @Transactional(readOnly = true)
    public Usuario.Role buscarRolePorUserName(String usuername) {
        return usuarioRepository.findRoleByUserName(usuername);
    }
}
