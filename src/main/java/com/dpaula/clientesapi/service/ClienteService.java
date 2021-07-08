package com.dpaula.clientesapi.service;

import com.dpaula.clientesapi.entity.Cliente;
import com.dpaula.clientesapi.error.ConflictException;
import com.dpaula.clientesapi.error.ObjectNotFoundException;
import com.dpaula.clientesapi.repository.ClienteRepository.ClienteRepository;
import com.dpaula.clientesapi.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

    public Cliente create(final Cliente cliente) {
        log.info("{} Criando novo cliente, email: {}", Util.LOG_PREFIX, cliente.getEmail());

        validarEmailConflito(cliente.getEmail());

        cliente.setDataInclusao(Util.getDataAtualDateTime());

        return repository.save(cliente);
    }

    private void validarEmailConflito(final String email) {
        if (repository.existsByEmail(email)) {
            throw new ConflictException("Já existe Cliente cadastrado para email: " + email);
        }
    }

    public Cliente alterar(final Cliente cliente) {
        log.info("{} Alterando cliente id: {}", Util.LOG_PREFIX, cliente.getId());

        final var clienteBase = findClienteByIdOrThrow(cliente.getId());

        if (!cliente.getEmail().equalsIgnoreCase(clienteBase.getEmail())) {
            validarEmailConflito(cliente.getEmail());
            clienteBase.setEmail(cliente.getEmail());
        }

        clienteBase.setNome(cliente.getNome());
        clienteBase.setDataNascimento(cliente.getDataNascimento());
        clienteBase.setDataAlteracao(Util.getDataAtualDateTime());

        return repository.save(clienteBase);
    }

    private Cliente findClienteByIdOrThrow(final UUID id) {
        return repository.findById(id)
            .orElseThrow(ObjectNotFoundException.with(Cliente.class, id, "id"));
    }

    public Cliente buscar(final UUID id) {
        log.info("{} Buscando cliente id: {}", Util.LOG_PREFIX, id);
        return findClienteByIdOrThrow(id);
    }

    public void delete(final UUID id) {
        log.info("{} Removendo cliente id: {}", Util.LOG_PREFIX, id);

        final var cliente = findClienteByIdOrThrow(id);
        repository.delete(cliente);
    }
}
