package com.dpaula.clientesapi.endpoint.impl;

import com.dpaula.clientesapi.endpoint.IClienteController;
import com.dpaula.clientesapi.service.ClienteService;
import com.dpaula.clientesapi.vo.ClienteVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author Fernando de Lima on 28/06/21
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClienteController implements IClienteController {

    private final ClienteService service;

    @Override
    public ResponseEntity<Page<ClienteVO>> listAll(final Pageable pageable) {
        final Page<ClienteVO> clientes = service.findAll(pageable).map(ClienteVO::parse);

        return ResponseEntity.ok(clientes);
    }
}
