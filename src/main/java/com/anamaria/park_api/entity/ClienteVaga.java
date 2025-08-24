package com.anamaria.park_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "clientes_tem_vagas")
@EntityListeners(AuditingEntityListener.class)
public class ClienteVaga {//classe de relacionamento entre cliente e vagas , como ela tem outras informaões que são importantes foi feito uma classe prara representar o relacionamneto, se n tivesse essa informaçoes o proprio hibernate faria essa classe no banco de dados

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_recibo", nullable = false, unique = true, length = 15)
    private String recibo;

    @Column(name = "placa", nullable = false, length = 8)
    private String placa;

    @Column(name = "marca", nullable = false, length = 45)
    private String marca;

    @Column(name = "modelo", nullable = false, length = 45)
    private String modelo;

    @Column(name = "cor", nullable = false, length = 45)
    private String cor;

    @Column(name = "data_entrada", nullable = false)
    private LocalDateTime dataEntrada;

    @Column(name = "data_saida")
    private LocalDateTime dataSaida;

    @Column(name = "valor", columnDefinition = "decimal(10,2)")//definição do tipo de coluna que vai ser do tipo decimal com no maximo 7 digitos e duas casas decimais
    private BigDecimal valor;

    @Column(name = "desconto", columnDefinition = "decimal(10,2)")
    private BigDecimal desconto;


    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_vaga")
    private Vaga vaga;

    @CreatedDate //da Auditoria compo utilizado no processo de auditoria
    @Column(name = "data_criação")
    private LocalDateTime dataCriação;

    @LastModifiedDate //da Auditoria compo utilizado no processo de auditoria
    @Column(name = "data_modificacao")
    private LocalDateTime dataModificacao;

    @CreatedBy //da Auditoria compo utilizado no processo de auditoria
    @Column(name = "criado_por")
    private String criadoPor;

    @LastModifiedBy //da Auditoria compo utilizado no processo de auditoria
    @Column(name = "modififaco_por")
    private String modififacoPor;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClienteVaga that = (ClienteVaga) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
