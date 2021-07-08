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
public class ErrorDetails {

    private final String message;
    private final int status;
    private final long timestamp;
    private final String url;
    private final DetailDeveloperMessage detailDeveloper;
}
