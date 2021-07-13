package com.dpaula.clientesapi.conf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author Fernando de Lima
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteConfig {

    @NotNull
    public Integer idadeMinima;
    @NotNull
    public Integer idadeMaxima;
}
