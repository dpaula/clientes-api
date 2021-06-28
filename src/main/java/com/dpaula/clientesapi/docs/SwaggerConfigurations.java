package com.dpaula.clientesapi.docs;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Fernando de Lima on 28/06/21
 */
@Configuration
public class SwaggerConfigurations {

    @Value("${info.app.version}")
    private String appVersion;

    @Bean
    public OpenAPI customCofiguration() {
        return new OpenAPI()
            .info(new Info().title("Clientes API")
                .description("Api para cadastro de Clientes")
                .version(appVersion)
                .license(new License()
                    .name("")
                    .url("http://unlicense.org")))
            .externalDocs(new ExternalDocumentation()
                .description("Api para cadastro de Clientes")
                .url("http://teste.dpaula.com.br/clientes-api/v1/suporte"));
    }
}