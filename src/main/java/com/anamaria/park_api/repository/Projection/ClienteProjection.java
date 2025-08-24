package com.anamaria.park_api.repository.Projection;

public interface ClienteProjection { //usada como dto apenas com as informações que quero retonar utilizando paginação

    Long getId();
    String getNome();
    String getCpf();
}
