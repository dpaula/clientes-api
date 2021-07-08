package com.dpaula.clientesapi.vo;

import com.dpaula.clientesapi.entity.Cliente;
import com.dpaula.clientesapi.util.Util;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_WRITE;

/**
 * @author Fernando de Lima on 28/06/21
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
@Schema(name = "Cliente")
public class ClienteDTO {

    @Schema(accessMode = READ_WRITE, example = "6a0f69fc-63c3-48e9-9f0a-efde73604d69", description = "Id do Cliente")
    private UUID id;

    @Schema(accessMode = READ_WRITE, example = "Fernando de Lima", description = "Nome Completo do Cliente")
    @NotEmpty
    private String nome;

    @Schema(accessMode = READ_WRITE, example = "fernando.dpaula@gmail.com", description = "E-mail do Cliente")
    @NotEmpty
    private String email;

    @Schema(accessMode = READ_WRITE, example = "30/12/1995", description = "Idade do Cliente")
    @JsonFormat(pattern = Util.DD_MM_YYYY)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @NotNull
    private LocalDate dataNascimento;

    @Schema(accessMode = READ_ONLY, example = "37", description = "Idade do Cliente")
    private Long idade;

    @Schema(accessMode = READ_ONLY, example = "2021-06-21T02:00:00", description = "Data e Hora do Cadastro do Cliente")
    private LocalDateTime dataInclusao;

    @Schema(accessMode = READ_ONLY, example = "2021-06-21T02:00:00", description = "Data e Hora da alteração Cadastro do Cliente")
    private LocalDateTime dataAlteracao;

    public static ClienteDTO parse(final Cliente cliente) {

        return ClienteDTO.builder()
            .id(cliente.getId())
            .nome(cliente.getNome())
            .email(cliente.getEmail())
            .dataNascimento(cliente.getDataNascimento())
            .idade(cliente.getIdade())
            .dataInclusao(cliente.getDataInclusao())
            .dataAlteracao(cliente.getDataAlteracao())
            .build();
    }
}
