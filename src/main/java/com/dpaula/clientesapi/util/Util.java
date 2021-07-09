package com.dpaula.clientesapi.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author Fernando de Lima on 28/06/21
 */
@Component
@Configuration
public class Util {

	public static String LOG_PREFIX;

	public static final ZoneId ZONA_ID = ZoneId.of("America/Sao_Paulo");

	public static final String DD_MM_YYYY = "dd/MM/yyyy";
	public static final DateTimeFormatter BRAZIL_DATE_FORMAT = DateTimeFormatter.ofPattern(DD_MM_YYYY);

	@Value("${server.undertow.accesslog.prefix}")
	public void setLogPrefix(final String logPrefix) {
		Util.LOG_PREFIX = logPrefix;
	}

	public static LocalDateTime getDataAtualDateTime() {
		return LocalDateTime.now(ZONA_ID);
	}

	public static LocalDate getDataAtualDate() {
		return LocalDate.now(ZONA_ID);
	}
}
