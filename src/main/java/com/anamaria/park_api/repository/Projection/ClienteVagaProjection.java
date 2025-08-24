package com.anamaria.park_api.repository.Projection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)//os tributos que forem nulos n seram mostrados
public interface ClienteVagaProjection {

     String getPlaca();

     String getMarca();

     String getmodelo();

     String getCor();

     String getClienteCpf();

     String getRecibo();

     @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
     LocalDateTime getDataEntrada();

     @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
     LocalDateTime getDataSaida();

     String getVagaCodigo();

     BigDecimal getValor();

     BigDecimal getDesconto();
}
