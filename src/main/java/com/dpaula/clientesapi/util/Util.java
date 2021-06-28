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

	public static final DateTimeFormatter FORMATER_DD_MM_YYYY_HH_MM_SS = DateTimeFormatter.ofPattern(
		"dd/MM/yyyy - HH:mm:ss");

	@Value("${server.undertow.accesslog.prefix}")
	public void setLogPrefix(final String logPrefix) {
		Util.LOG_PREFIX = logPrefix;
	}

	public static LocalDate getDataAtual() {
		return LocalDate.now(ZONA_ID);
	}

	public static LocalDateTime getDataAtualDateTime() {
		return LocalDateTime.now(ZONA_ID);
	}

}
