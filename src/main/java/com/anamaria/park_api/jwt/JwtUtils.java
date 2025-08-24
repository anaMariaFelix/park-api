package com.anamaria.park_api.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
public class JwtUtils {
    public static final String JWT_BEARER = "Bearer";
    public static final String JWT_AUTHORIZATION = "Authorization";
    public static final String SECRET_KEY = "0123456789-0123456789-0123456789"; // a chave segreta tem que ter 32 caracteres se tiver menos da erro.
    public static final Long EXPIRE_DAYS = 0L;
    public static final Long EXPIRE_HOURS = 0L;
    public static final Long EXPIRE_MINUTES = 30L;

    private JwtUtils(){

    }

    //retorna a chave com os padores necessarios para o uso da criptografia
    private static Key generatyKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)); //hmacShaKeyFor responsavel por preparar a chave para ser criptografada no momento em que for gerar o token
    }

    //calculo refente a inpiração do token
    private static Date toExpireDate(Date start){ //start guarda o valor da data inicial
        LocalDateTime dateTime = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();//converte a data que chegou em um localDateTime
        LocalDateTime and = dateTime.plusDays(EXPIRE_DAYS).plusHours(EXPIRE_HOURS).plusMinutes(EXPIRE_MINUTES);
        return Date.from(and.atZone(ZoneId.systemDefault()).toInstant());
    }

    //metodo que vai gerar o token
    public static JwtToken createToken(String userName, String role){
        Date issuedAt = new Date(); //gera a data inicial do token, ou seja a data de criação
        Date limit = toExpireDate(issuedAt); //chama o metodo que calcula a data limite do token, para ter a data limite dele

        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT")//define que o token é do tipo JWT
                .setSubject(userName)//é interessante usar ou o id ou ussename no subject, porque ele é usado para confirmar se o token é de um usuario existente na aplicação, o valor do subject é usado para fazer a consulta no banco
                .setIssuedAt(issuedAt)//metodo que recebe a data de geração do token
                .setExpiration(limit)//data de expiração do token
                .signWith(generatyKey(), SignatureAlgorithm.HS256)//generatyKey assinatura do token, SignatureAlgorithm.HS256 o tipo de criptografia
                .claim("role", role) //pode ser usado para defir o tipo do perfil de usuario, esse metodo claim pode ser usado para varias coisas, por exemplo quando vc quer adicionar alguma informação no token mais n existe um metodo especifo pra isso, ai usasse o claim
                .compact(); //transforma o objeto em um token no formato de string que tenha o padrao embase64 seprada cada uma das partes por prontos.

        return new JwtToken(token);
    }

    //recupera o conteudo do token
    private static Claims getClaimsFromToken(String token){
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(generatyKey()).build()//verifica se a assinatura que o cliente enviou e igua a assinatura que temos gerada na class
                    .parseClaimsJws(refactorToken(token)).getBody();//recupera o corpo do token

        }catch (JwtException e){
            log.error(String.format("Token invalido %s", e.getMessage()));
        }
        return null; //pq se o metodo cair no catch ele faz as coisa do catch e pois retorna null
    }

    //recupera o userName que esta dentro do token
    public static String getUserNameFromTokem(String token){
        return getClaimsFromToken(token).getSubject();
    }

    //testa a validade do token, que pode ser invalido porque a data do token inspirou ou porque a assinatura do token n é igual a gerada na classe
    public static boolean isTokenValid(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(generatyKey()).build()//verifica se a assinatura que o cliente enviou e igua a assinatura que temos gerada na class
                    .parseClaimsJws(refactorToken(token));//recupera o corpo do token
            return true;
        }catch (JwtException e){
            log.error(String.format("Token invalido %s", e.getMessage()));
        }
        return false;
    }

    //remove do token a instrução bearer
    private static String refactorToken(String token){
        if(token.contains(JWT_BEARER)){
            return token.substring(JWT_BEARER.length());
        }
        return token;
    }
}
