package com.dpaula.clientesapi.handler;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Fernando de Lima
 */
@Builder
@Getter
@ToString
public class DetailDeveloperMessage {
    private final String exceptionClass;
    private final String error;
    private final String stackTrace;
}
