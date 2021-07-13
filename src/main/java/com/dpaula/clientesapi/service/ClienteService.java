package com.dpaula.clientesapi.service;

import com.dpaula.clientesapi.conf.ConfiguracoesGerais;
import com.dpaula.clientesapi.entity.Cliente;
import com.dpaula.clientesapi.error.ConflictException;
import com.dpaula.clientesapi.error.ObjectNotFoundException;
import com.dpaula.clientesapi.error.UnprocessableEntityException;
import com.dpaula.clientesapi.filtro.ClienteFiltro;
import com.dpaula.clientesapi.repository.ClienteRepository.ClienteRepository;
import com.dpaula.clientesapi.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Fernando de Lima on 28/06/21
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClienteService {

    private final ClienteRepository repository;
    private final ConfiguracoesGerais configuracoesGerais;

    @Cacheable(value = "cliente")
    public Page<Cliente> findAllByFilters(final String nome, final String email,
        final Integer idade,
        final LocalDate dataNascimento, final Pageable pageable) {

        final var filtro = ClienteFiltro.montarFiltro(nome, email, idade, dataNascimento);

        log.info("{} Buscando clientes para o filtro {}", Util.LOG_PREFIX, filtro);

        final var clienteSpec = filtro.toSpec();

        return repository.findAll(clienteSpec, pageable);
    }

    @CachePut(value = "cliente", key = "#cliente.id")
    @CacheEvict(value = "cliente", allEntries = true)
    public Cliente create(final Cliente cliente) {
        validarPoliticaIdades(cliente);
        log.info("{} Criando novo cliente, email: {}", Util.LOG_PREFIX, cliente.getEmail());

        validarEmailConflito(cliente.getEmail());

        cliente.setDataInclusao(Util.getDataAtualDateTime());

        return repository.save(cliente);
    }

    private void validarPoliticaIdades(final @NotNull Cliente cliente) {
        if (cliente.getIdade() > configuracoesGerais.getCliente().getIdadeMaxima()) {
            throw new UnprocessableEntityException(
                "Idade ultrapassa política de idade máxima: " + configuracoesGerais.getCliente().getIdadeMaxima());
        }
        if (cliente.getIdade() < configuracoesGerais.getCliente().getIdadeMinima()) {
            throw new UnprocessableEntityException(
                "Idade ultrapassa política de idade mínima: " + configuracoesGerais.getCliente().getIdadeMinima());
        }
    }

    private void validarEmailConflito(final String email) {
        if (repository.existsByEmail(email)) {
            throw new ConflictException("Já existe Cliente cadastrado para email: " + email);
        }
    }

    @CacheEvict(value = "cliente", allEntries = true)
    public Cliente alterar(final Cliente cliente) {
        validarPreCondicoesAlterar(cliente);
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

    private void validarPreCondicoesAlterar(final @NotNull Cliente cliente) {
        if (cliente.getId() == null) {
            throw new UnprocessableEntityException("Id deve ser informado para alterar Cliente!");
        }
        validarPoliticaIdades(cliente);
    }

    private Cliente findClienteByIdOrThrow(final UUID id) {
        return repository.findById(id)
            .orElseThrow(ObjectNotFoundException.with(Cliente.class, id, "id"));
    }

    @Cacheable(value = "cliente", key = "#id")
    public Cliente buscar(final UUID id) {
        log.info("{} Buscando cliente id: {}", Util.LOG_PREFIX, id);
        return findClienteByIdOrThrow(id);
    }

    @CacheEvict(value = "cliente", allEntries = true)
    public void delete(final UUID id) {
        log.info("{} Removendo cliente id: {}", Util.LOG_PREFIX, id);

        final var cliente = findClienteByIdOrThrow(id);
        repository.delete(cliente);
    }
}
