package com.dpaula.clientesapi.filtro;

import com.dpaula.clientesapi.entity.Cliente;
import com.dpaula.clientesapi.specification.ClienteSpecs;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Optional;

/**
 * @author Fernando de Lima on 09/07/21
 */
@Data
@Slf4j
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ClienteFiltro {

    private String nome;
    private String email;
    private Integer idade;
    private LocalDate dataNascimento;

    public static ClienteFiltro montarFiltro(final String nome, final String email,
        final Integer idade,
        final LocalDate dataNascimento) {
        return ClienteFiltro.builder()
            .nome(nome)
            .email(email)
            .idade(idade)
            .dataNascimento(dataNascimento)
            .build();
    }

    public Specification<Cliente> toSpec() {

        final var specNome = Optional.ofNullable(nome)
            .map(ClienteSpecs::nome)
            .orElse(Specification.where(null));

        final var specEmail = Optional.ofNullable(email)
            .map(ClienteSpecs::email)
            .orElse(Specification.where(null));

        final var specIdade = Optional.ofNullable(idade)
            .map(ClienteSpecs::idade)
            .orElse(Specification.where(null));

        final var specDataNascimento = Optional.ofNullable(dataNascimento)
            .map(ClienteSpecs::dataNascimento)
            .orElse(Specification.where(null));

        return specNome.and(specEmail)
            .and(specIdade)
            .and(specDataNascimento);
    }
}
