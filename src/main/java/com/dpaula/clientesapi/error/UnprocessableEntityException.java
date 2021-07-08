package com.dpaula.clientesapi.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Fernando de Lima
 */
@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
public class UnprocessableEntityException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnprocessableEntityException(final String msg) {
        super(msg);
    }
}
