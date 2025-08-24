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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "vagas")
@EntityListeners(AuditingEntityListener.class)
public class Vaga implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 4)
    private String codigo;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusVaga status;

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

    public enum StatusVaga{
        LIVRE, OCUPADA
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vaga vaga = (Vaga) o;
        return Objects.equals(id, vaga.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
