package com.dpaula.clientesapi.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.function.Supplier;

/**
 * @author Fernando de Lima
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Objeto não encontrado")
public class ObjectNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -4129510107701203676L;

    public <T> ObjectNotFoundException(final Class<T> clazz, final Object field, final String fieldName) {
        super("Objeto não encontrado: <"
            .concat(fieldName)
            .concat(">: <")
            .concat(field.toString())
            .concat("> Class: <")
            .concat(clazz.getName())
            .concat(">"));
    }

    public static <T> Supplier<ObjectNotFoundException> with(final Class<T> clazz, final Object field,
        final String fieldName) {
        return () -> new ObjectNotFoundException(clazz, field, fieldName);
    }
}
