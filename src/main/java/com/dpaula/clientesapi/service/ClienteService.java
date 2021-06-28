package com.dpaula.clientesapi.service;

import com.dpaula.clientesapi.entity.Cliente;
import com.dpaula.clientesapi.repository.ClienteRepository.ClienteRepository;
import com.dpaula.clientesapi.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author Fernando de Lima on 28/06/21
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClienteService {

    private final ClienteRepository repository;

    public Page<Cliente> findAll(final Pageable pageable) {
        log.info("{} Listando todos os Clientes", Util.LOG_PREFIX);

        return repository.findAll(pageable);
    }
}
