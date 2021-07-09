package com.dpaula.clientesapi.specification;

import com.dpaula.clientesapi.entity.Cliente;
import com.dpaula.clientesapi.util.Util;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/**
 * @author Fernando de Lima on 09/07/21
 */
public class ClienteSpecs {

    private static final String PERCENT = "%";

    public static Specification<Cliente> nome(final String nome) {
        return (root, query, builder) -> builder
            .like(builder.upper(root.get("nome")), likeAll(nome.trim().toUpperCase()));
    }

    public static Specification<Cliente> email(final String email) {
        return (root, query, builder) -> builder
            .like(builder.upper(root.get("email")), likeAll(email.trim().toUpperCase()));
    }

    public static Specification<Cliente> dataNascimento(final LocalDate dataNascimento) {
        return (root, query, builder) -> builder
            .equal(root.get("dataNascimento"), dataNascimento);
    }

    public static Specification<Cliente> idade(final Integer idade) {

        final var dataAtual = Util.getDataAtualDate();

        final LocalDate dataInicial = dataAtual.minusYears(idade).minusYears(1).plusDays(1);
        final LocalDate dataFinal = dataAtual.minusYears(idade);

        return (root, query, builder) -> builder
            .between(root.get("dataNascimento"), dataInicial, dataFinal);
    }

    private static String likeAll(final String field) {
        return PERCENT + field + PERCENT;
    }
}
