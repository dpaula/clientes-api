package com.dpaula.clientesapi.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Fernando de Lima
 */
@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Conflict")
public class ConflictException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ConflictException(final String msg) {
        super(msg);
    }
}