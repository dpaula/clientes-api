package com.dpaula.clientesapi.handler;

import com.dpaula.clientesapi.error.ConflictException;
import com.dpaula.clientesapi.error.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;

/**
 * @author Fernando de Lima
 */
@ControllerAdvice
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RestExceptionHandler {

    public static final String ERRO_INTERNO_NO_SERVIDOR = "Erro interno no servidor!";
    public static final String INFORMACOES_INCONSISTENTES = "Informações inconsistentes!";
    public static final String CONFLITO_NAS_INFORMACOES = "Não foi possível salvar as informações!";
    public static final String INFORMACOES_NAO_ENCONTRADAS = "Não encontrada informações solicitadas!";
    private final MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(final HttpServletRequest req, final Exception exception) {

        if (exception instanceof HttpMessageNotReadableException) {

            final HttpMessageNotReadableException excep = (HttpMessageNotReadableException) exception;
            final var details = buildErrorDetails(excep, req, INFORMACOES_INCONSISTENTES, HttpStatus.BAD_REQUEST,
                excep.getMessage());
            return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
        }

        final var details = buildErrorDetails(exception, req, ERRO_INTERNO_NO_SERVIDOR,
            HttpStatus.INTERNAL_SERVER_ERROR,
            exception.getMessage());

        return new ResponseEntity<>(details, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(final HttpServletRequest req,
        final MethodArgumentNotValidException exception) {

        var mensagem = exception.getMessage();

        final var fieldErrors = exception.getBindingResult()
            .getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            final var e = fieldErrors.get(0);
            mensagem = e.getField() + " " + messageSource.getMessage(e, LocaleContextHolder.getLocale());
        }
        final var details = buildErrorDetails(exception, req, INFORMACOES_INCONSISTENTES, HttpStatus.BAD_REQUEST,
            mensagem);
        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleConflictException(final HttpServletRequest req,
        final ConflictException exception) {
        final var details = buildErrorDetails(exception, req, CONFLITO_NAS_INFORMACOES, HttpStatus.CONFLICT,
            exception.getMessage());
        return new ResponseEntity<>(details, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<?> handleObjectNotFoundException(final HttpServletRequest req,
        final ObjectNotFoundException exception) {
        final var details = buildErrorDetails(exception, req, INFORMACOES_NAO_ENCONTRADAS, HttpStatus.NOT_FOUND,
            exception.getMessage());
        return new ResponseEntity<>(details, HttpStatus.NOT_FOUND);
    }

    private ErrorDetails buildErrorDetails(final Exception exception, final HttpServletRequest req,
        final String message,
        final HttpStatus httpStatus, final String detailveloperMessage) {
        return ErrorDetails.builder()
            .message(message)
            .status(httpStatus.value())
            .timestamp(new Date().getTime())
            .url(req.getRequestURI())
            .detailDeveloper(DetailDeveloperMessage.builder()
                .exceptionClass(exception.getClass()
                    .getName())
                .error(detailveloperMessage)
                .stackTrace(Arrays.toString(exception.getStackTrace()))
                .build())
            .build();
    }
}
