package com.anamaria.park_api.web.controller.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)//os tributos que forem nulos n seram mostrados
public class EstacionamentoResponseDTO {

    private String placa;

    private String marca;

    private String modelo;

    private String cor;

    private String clienteCpf;

    private String recibo;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime dataEntrada;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime dataSaida;

    private String vagaCodigo;

    private BigDecimal valor;

    private BigDecimal desconto;
}
