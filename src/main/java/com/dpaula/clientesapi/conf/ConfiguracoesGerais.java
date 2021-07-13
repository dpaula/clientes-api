package com.dpaula.clientesapi.conf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * @author Fernando de Lima on 13/07/21
 */
@Builder
@Data
@Component
@Validated
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "app.config")
public class ConfiguracoesGerais {

    @NotNull
    private ClienteConfig cliente;
}
