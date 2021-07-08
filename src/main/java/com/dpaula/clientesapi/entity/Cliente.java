package com.dpaula.clientesapi.entity;

import com.dpaula.clientesapi.util.Util;
import com.dpaula.clientesapi.vo.ClienteDTO;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.YEARS;

/**
 * @author Fernando de Lima on 28/06/21
 */
@Data
@Entity
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cli_clientes")
public class Cliente {

    @Id
    @Type(type = "uuid-char")
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "generator", strategy = "uuid2")
    @Column(nullable = false, columnDefinition = "uniqueidentifier")
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String email;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "data_inclusao")
    private LocalDateTime dataInclusao;

    @Column(name = "data_alteracao")
    private LocalDateTime dataAlteracao;

    public Long getIdade() {
        if (dataNascimento == null) {
            return null;
        }

        return YEARS.between(dataNascimento, LocalDate.now(Util.ZONA_ID));
    }

    public static Cliente parse(final ClienteDTO cliente) {
        return Cliente.builder()
            .id(cliente.getId())
            .nome(cliente.getNome())
            .email(cliente.getEmail())
            .dataNascimento(cliente.getDataNascimento())
            .dataAlteracao(cliente.getDataAlteracao())
            .build();
    }
}
