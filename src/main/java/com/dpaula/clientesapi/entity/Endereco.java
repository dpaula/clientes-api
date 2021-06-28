package com.dpaula.clientesapi.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

/**
 * @author Fernando de Lima on 28/06/21
 */
@Data
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cli_enderecos")
@Builder
public class Endereco {

    @Id
    @Type(type = "uuid-char")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "generator", strategy = "uuid2")
    @Column(nullable = false, columnDefinition = "uniqueidentifier")
    private UUID id;

    private String descricao;

    private String rua;

    private Integer numero;

    private String cep;

    private String referencia;

    private String uf;

    private String cidade;
}
