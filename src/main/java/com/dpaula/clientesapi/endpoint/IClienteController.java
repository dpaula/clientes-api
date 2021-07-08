package com.dpaula.clientesapi.endpoint;

import com.dpaula.clientesapi.vo.ClienteDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * @author Fernando de Lima on 28/06/21
 */
@RestController
@RequestMapping("/v1/clientes")
@CrossOrigin(origins = "*")
@Tag(name = "Clientes", description = "Serviços para gerenciamento de Clientes")
public interface IClienteController {

    @Operation(summary = "Listar Clientes", description = "Consulta todos os clientes, ordenado por data de cadastro", tags = "Clientes")
    @GetMapping
    ResponseEntity<Page<ClienteDTO>> listAll(
        @PageableDefault(sort = "dataInclusao", direction = Sort.Direction.DESC) @Parameter(hidden = true) Pageable pageable);

    @Operation(summary = "Incluir Cliente", description = "Post para incluir um novo cliente")
    @PostMapping
    ResponseEntity<ClienteDTO> post(@NotNull @Valid @RequestBody final ClienteDTO clienteInput,
        UriComponentsBuilder builder);

    @Operation(summary = "Alterar Cliente", description = "Put para alterar um cliente")
    @PutMapping
    ResponseEntity<ClienteDTO> put(@NotNull @Valid @RequestBody final ClienteDTO clienteInput);

    @Operation(summary = "Buscar Cliente", description = "Get para buscar um Cliente pelo Id")
    @GetMapping(value = "/{id}")
    ResponseEntity<ClienteDTO> get(@PathVariable UUID id);

    @Operation(summary = "Remover Cliente", description = "Delete para remover um Cliente pelo Id")
    @DeleteMapping(value = "/{id}")
    @Transactional
    void delete(@PathVariable UUID id);
}
