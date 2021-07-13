package com.dpaula.clientesapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ClientesApiApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ClientesApiApplication.class, args);
    }
}
