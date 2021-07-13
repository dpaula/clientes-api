package com.dpaula.clientesapi.dto;

import com.dpaula.clientesapi.entity.Cliente;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
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
@RedisHash("clientes")
public class ClienteDTO implements Serializable {

    private static final long serialVersionUID = 8926274467059821529L;
    @Schema(accessMode = READ_WRITE, example = "6a0f69fc-63c3-48e9-9f0a-efde73604d69", description = "Id do Cliente")
    private UUID id;

    @Schema(accessMode = READ_WRITE, example = "Fernando de Lima", description = "Nome Completo do Cliente")
    @NotEmpty
    @Size(max = 200)
    private String nome;

    @Schema(accessMode = READ_WRITE, example = "fernando.dpaula@gmail.com", description = "E-mail do Cliente")
    @NotEmpty
    @Size(max = 100)
    @Email(message = "Deve ser endereço de email válido!")
    private String email;

    @Schema(accessMode = READ_WRITE, example = "1995-12-30", description = "Idade do Cliente", pattern = "teste")
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
