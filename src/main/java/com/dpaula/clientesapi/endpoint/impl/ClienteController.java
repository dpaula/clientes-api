package com.dpaula.clientesapi.endpoint.impl;

import com.dpaula.clientesapi.endpoint.IClienteController;
import com.dpaula.clientesapi.entity.Cliente;
import com.dpaula.clientesapi.service.ClienteService;
import com.dpaula.clientesapi.vo.ClienteDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

/**
 * @author Fernando de Lima on 28/06/21
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClienteController implements IClienteController {

    private final ClienteService service;

    @Override
    public ResponseEntity<Page<ClienteDTO>> listAll(final Pageable pageable) {
        final Page<ClienteDTO> clientes = service.findAll(pageable).map(ClienteDTO::parse);
        return ResponseEntity.ok(clientes);
    }

    @Override
    public ResponseEntity<ClienteDTO> post(final ClienteDTO clienteInput, final UriComponentsBuilder builder) {
        final var cliente = Cliente.parse(clienteInput);
        final var clienteCriado = ClienteDTO.parse(service.create(cliente));

        final var location = builder.replacePath("/v1/clientes/{id}")
            .buildAndExpand(clienteCriado.getId()
                .toString())
            .toUri();
        return ResponseEntity.created(location)
            .body(clienteCriado);
    }

    @Override
    public ResponseEntity<ClienteDTO> put(final ClienteDTO clienteInput) {
        final var cliente = Cliente.parse(clienteInput);
        return ResponseEntity.ok(ClienteDTO.parse(service.alterar(cliente)));
    }

    @Override
    public ResponseEntity<ClienteDTO> get(final UUID id) {
        return ResponseEntity.ok(ClienteDTO.parse(service.buscar(id)));
    }

    @Override
    public void delete(final UUID id) {
        service.delete(id);
    }
}
