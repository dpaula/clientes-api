package com.dpaula.clientesapi;

import com.dpaula.clientesapi.conf.ConfiguracoesGerais;
import com.dpaula.clientesapi.dto.ClienteDTO;
import com.dpaula.clientesapi.entity.Cliente;
import com.dpaula.clientesapi.repository.ClienteRepository.ClienteRepository;
import com.dpaula.clientesapi.util.JsonUtilsTest;
import com.dpaula.clientesapi.util.Util;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.YEARS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WebAppConfiguration
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ClientesApiApplicationTests {

    private static final String URI_CLIENTES = "/v1/clientes";
    private static final String URI_CLIENTES_ID = "/v1/clientes/{id}";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository repository;

    @Autowired
    private ConfiguracoesGerais configuracoesGerais;

    @Test
    void validarPost() throws Exception {

        final ClienteDTO clienteDTO = criarClienteDTO();

        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(URI_CLIENTES)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(JsonUtilsTest.toJson(clienteDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.nome", is(clienteDTO.getNome())))
            .andExpect(jsonPath("$.email", is(clienteDTO.getEmail())))
            .andExpect(jsonPath("$.dataNascimento", is(clienteDTO.getDataNascimento().format(
                Util.DATE_FORMAT))))
            .andExpect(jsonPath("$.dataInclusao").isNotEmpty())
            .andExpect(jsonPath("$.idade", is((int) (getIdade(clienteDTO.getDataNascimento())))))
            .andReturn();

        final MockHttpServletResponse response = result.getResponse();

        final UUID id = UUID.fromString(new JSONObject(response.getContentAsString()).getString("id"));

        final Optional<Cliente> clienteOptional = repository.findById(id);

        assertThat(clienteOptional.isPresent()).isTrue();

        final Cliente cliente = clienteOptional.get();
        assertThat(cliente.getNome()).isEqualTo(clienteDTO.getNome());
        assertThat(cliente.getEmail()).isEqualTo(clienteDTO.getEmail());
        assertThat(cliente.getDataNascimento()).isEqualTo(clienteDTO.getDataNascimento());
        assertThat(cliente.getDataInclusao()).isNotNull();
        assertThat(cliente.getIdade()).isEqualTo(getIdade(clienteDTO.getDataNascimento()));
    }

    @Test
    void validarPostConflictException() throws Exception {

        criarClienteSave();
        final ClienteDTO clienteDTO = criarClienteDTO();

        mockMvc.perform(MockMvcRequestBuilders.post(URI_CLIENTES)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(JsonUtilsTest.toJson(clienteDTO)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message", is("N??o foi poss??vel salvar as informa????es!")))
            .andExpect(jsonPath("$.status", is(HttpStatus.CONFLICT.value())))
            .andExpect(jsonPath("$.url", is(URI_CLIENTES)))
            .andExpect(
                jsonPath("$.detailDeveloper.exceptionClass", is("com.dpaula.clientesapi.error.ConflictException")))
            .andExpect(jsonPath("$.detailDeveloper.error",
                is("J?? existe Cliente cadastrado para email: fernando.dpaula@gmail.com")));
    }

    @Test
    void validarPostClienteNomeNull() throws Exception {

        final ClienteDTO clienteDTO = ClienteDTO.builder()
            .email("fernando.dpaula@gmail.com")
            .dataNascimento(LocalDate.of(1983, 12, 30))
            .build();

        mockMvc.perform(MockMvcRequestBuilders.post(URI_CLIENTES)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(JsonUtilsTest.toJson(clienteDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Informa????es inconsistentes!")))
            .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
            .andExpect(jsonPath("$.url", is(URI_CLIENTES)))
            .andExpect(
                jsonPath("$.detailDeveloper.exceptionClass",
                    is("org.springframework.web.bind.MethodArgumentNotValidException")))
            .andExpect(jsonPath("$.detailDeveloper.error",
                is("nome must not be empty")));
    }

    @Test
    void validarPostClienteEmailNull() throws Exception {

        final ClienteDTO clienteDTO = ClienteDTO.builder()
            .nome("Fernando de Lima")
            .dataNascimento(LocalDate.of(1983, 12, 30))
            .build();

        mockMvc.perform(MockMvcRequestBuilders.post(URI_CLIENTES)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(JsonUtilsTest.toJson(clienteDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Informa????es inconsistentes!")))
            .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
            .andExpect(jsonPath("$.url", is(URI_CLIENTES)))
            .andExpect(
                jsonPath("$.detailDeveloper.exceptionClass",
                    is("org.springframework.web.bind.MethodArgumentNotValidException")))
            .andExpect(jsonPath("$.detailDeveloper.error",
                is("email must not be empty")));
    }

    @Test
    void validarPostClienteEmailInvalido() throws Exception {

        final ClienteDTO clienteDTO = ClienteDTO.builder()
            .nome("Fernando de Lima")
            .email("fernando.dpaula")
            .dataNascimento(LocalDate.of(1983, 12, 30))
            .build();

        mockMvc.perform(MockMvcRequestBuilders.post(URI_CLIENTES)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(JsonUtilsTest.toJson(clienteDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Informa????es inconsistentes!")))
            .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
            .andExpect(jsonPath("$.url", is(URI_CLIENTES)))
            .andExpect(
                jsonPath("$.detailDeveloper.exceptionClass",
                    is("org.springframework.web.bind.MethodArgumentNotValidException")))
            .andExpect(jsonPath("$.detailDeveloper.error",
                is("email Deve ser endere??o de email v??lido!")));
    }

    @Test
    void validarPostClienteDataNascimentoNull() throws Exception {

        final ClienteDTO clienteDTO = ClienteDTO.builder()
            .nome("Fernando de Lima")
            .email("fernando.dpaula@gmail.com")
            .build();

        mockMvc.perform(MockMvcRequestBuilders.post(URI_CLIENTES)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(JsonUtilsTest.toJson(clienteDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Informa????es inconsistentes!")))
            .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
            .andExpect(jsonPath("$.url", is(URI_CLIENTES)))
            .andExpect(
                jsonPath("$.detailDeveloper.exceptionClass",
                    is("org.springframework.web.bind.MethodArgumentNotValidException")))
            .andExpect(jsonPath("$.detailDeveloper.error",
                is("dataNascimento must not be null")));
    }

    @Test
    void validarPostClienteDataNascimentoComDesacordoPoliticaIdadeMinima() throws Exception {

        final ClienteDTO clienteDTO = ClienteDTO.builder()
            .nome("Fernando de Lima")
            .email("fernando.dpaula@gmail.com")
            .dataNascimento(LocalDate.of(2005, 12, 30))
            .build();

        mockMvc.perform(MockMvcRequestBuilders.post(URI_CLIENTES)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(JsonUtilsTest.toJson(clienteDTO)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.message",
                is("Idade ultrapassa pol??tica de idade m??nima: " + configuracoesGerais.getCliente().getIdadeMinima())))
            .andExpect(jsonPath("$.status", is(HttpStatus.UNPROCESSABLE_ENTITY.value())))
            .andExpect(jsonPath("$.url", is(URI_CLIENTES)))
            .andExpect(
                jsonPath("$.detailDeveloper.exceptionClass",
                    is("com.dpaula.clientesapi.error.UnprocessableEntityException")))
            .andExpect(jsonPath("$.detailDeveloper.error",
                is("Idade ultrapassa pol??tica de idade m??nima: " + configuracoesGerais.getCliente().getIdadeMinima())));
    }

    @Test
    void validarPostClienteDataNascimentoComDesacordoPoliticaIdadeMaxima() throws Exception {

        final ClienteDTO clienteDTO = ClienteDTO.builder()
            .nome("Fernando de Lima")
            .email("fernando.dpaula@gmail.com")
            .dataNascimento(LocalDate.of(1878, 12, 30))
            .build();

        mockMvc.perform(MockMvcRequestBuilders.post(URI_CLIENTES)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(JsonUtilsTest.toJson(clienteDTO)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.message",
                is("Idade ultrapassa pol??tica de idade m??xima: " + configuracoesGerais.getCliente().getIdadeMaxima())))
            .andExpect(jsonPath("$.status", is(HttpStatus.UNPROCESSABLE_ENTITY.value())))
            .andExpect(jsonPath("$.url", is(URI_CLIENTES)))
            .andExpect(
                jsonPath("$.detailDeveloper.exceptionClass",
                    is("com.dpaula.clientesapi.error.UnprocessableEntityException")))
            .andExpect(jsonPath("$.detailDeveloper.error",
                is("Idade ultrapassa pol??tica de idade m??xima: " + configuracoesGerais.getCliente().getIdadeMaxima())));
    }

    @Test
    void validarPut() throws Exception {

        final String nome = "Fernando de Lima";
        final String email = "fernando.dpaula@gmail.com";
        final LocalDate dataNascimento = LocalDate.of(1983, 12, 30);

        final Cliente clienteSave = repository.save(Cliente.builder()
            .nome(nome)
            .email(email)
            .dataNascimento(dataNascimento)
            .dataInclusao(LocalDateTime.now())
            .build());

        final ClienteDTO clienteDTO = ClienteDTO.builder()
            .id(clienteSave.getId())
            .nome("Fernando de Paula de Lima")
            .email("fernando.dpaula@hotmail.com")
            .dataNascimento(LocalDate.of(1993, 12, 30))
            .build();

        mockMvc.perform(MockMvcRequestBuilders.put(URI_CLIENTES)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(JsonUtilsTest.toJson(clienteDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.nome", is(clienteDTO.getNome())))
            .andExpect(jsonPath("$.email", is(clienteDTO.getEmail())))
            .andExpect(jsonPath("$.dataNascimento", is(clienteDTO.getDataNascimento().format(
                Util.DATE_FORMAT))))
            .andExpect(jsonPath("$.dataInclusao").isNotEmpty())
            .andExpect(jsonPath("$.dataAlteracao").isNotEmpty())
            .andExpect(jsonPath("$.idade", is((int) (getIdade(clienteDTO.getDataNascimento())))));
    }

    @Test
    void validarPutClienteNotFound() throws Exception {

        final String nome = "Fernando de Lima";
        final String email = "fernando.dpaula@gmail.com";
        final LocalDate dataNascimento = LocalDate.of(1983, 12, 30);

        repository.save(Cliente.builder()
            .nome(nome)
            .email(email)
            .dataNascimento(dataNascimento)
            .dataInclusao(LocalDateTime.now())
            .build());

        final UUID id = UUID.randomUUID();
        final ClienteDTO clienteDTO = ClienteDTO.builder()
            .id(id)
            .nome("Fernando de Paula de Lima")
            .email("fernando.dpaula@hotmail.com")
            .dataNascimento(LocalDate.of(1993, 12, 30))
            .build();

        mockMvc.perform(MockMvcRequestBuilders.put(URI_CLIENTES)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(JsonUtilsTest.toJson(clienteDTO)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", is("N??o encontrada informa????es solicitadas!")))
            .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
            .andExpect(jsonPath("$.url", is(URI_CLIENTES)))
            .andExpect(
                jsonPath("$.detailDeveloper.exceptionClass",
                    is("com.dpaula.clientesapi.error.ObjectNotFoundException")))
            .andExpect(jsonPath("$.detailDeveloper.error",
                is("Objeto n??o encontrado: <id>: <" + id + "> Class: <com.dpaula.clientesapi.entity.Cliente>")));
    }

    @Test
    void validarPutClienteEmailConflictException() throws Exception {

        final String nome = "Fernando de Lima";
        final String email = "fernando.dpaula@gmail.com";
        final LocalDate dataNascimento = LocalDate.of(1983, 12, 30);

        final Cliente clienteSave = repository.save(Cliente.builder()
            .nome(nome)
            .email(email)
            .dataNascimento(dataNascimento)
            .dataInclusao(LocalDateTime.now())
            .build());

        final String emailJaCadastrado = "fernando.dpaula@hotmail.com";
        repository.save(Cliente.builder()
            .nome("Fernando de Paula")
            .email(emailJaCadastrado)
            .dataNascimento(LocalDate.of(1998, 12, 30))
            .dataInclusao(LocalDateTime.now())
            .build());

        final ClienteDTO clienteDTO = ClienteDTO.builder()
            .id(clienteSave.getId())
            .nome("Fernando de Paula de Lima")
            .email(emailJaCadastrado)
            .dataNascimento(LocalDate.of(1993, 12, 30))
            .build();

        mockMvc.perform(MockMvcRequestBuilders.put(URI_CLIENTES)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(JsonUtilsTest.toJson(clienteDTO)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message", is("N??o foi poss??vel salvar as informa????es!")))
            .andExpect(jsonPath("$.status", is(HttpStatus.CONFLICT.value())))
            .andExpect(jsonPath("$.url", is(URI_CLIENTES)))
            .andExpect(
                jsonPath("$.detailDeveloper.exceptionClass", is("com.dpaula.clientesapi.error.ConflictException")))
            .andExpect(jsonPath("$.detailDeveloper.error",
                is("J?? existe Cliente cadastrado para email: " + emailJaCadastrado)));
    }

    @Test
    void validarPutClienteDataNascimentoComDesacordoPoliticaIdadeMinima() throws Exception {

        final String nome = "Fernando de Lima";
        final String email = "fernando.dpaula@gmail.com";
        final LocalDate dataNascimento = LocalDate.of(1983, 12, 30);

        final Cliente clienteSave = repository.save(Cliente.builder()
            .nome(nome)
            .email(email)
            .dataNascimento(dataNascimento)
            .dataInclusao(LocalDateTime.now())
            .build());

        final ClienteDTO clienteDTO = ClienteDTO.builder()
            .id(clienteSave.getId())
            .nome("Fernando de Paula de Lima")
            .email("fernando.dpaula@hotmail.com")
            .dataNascimento(LocalDate.of(2005, 12, 30))
            .build();

        mockMvc.perform(MockMvcRequestBuilders.put(URI_CLIENTES)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(JsonUtilsTest.toJson(clienteDTO)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.message",
                is("Idade ultrapassa pol??tica de idade m??nima: " + configuracoesGerais.getCliente().getIdadeMinima())))
            .andExpect(jsonPath("$.status", is(HttpStatus.UNPROCESSABLE_ENTITY.value())))
            .andExpect(jsonPath("$.url", is(URI_CLIENTES)))
            .andExpect(
                jsonPath("$.detailDeveloper.exceptionClass",
                    is("com.dpaula.clientesapi.error.UnprocessableEntityException")))
            .andExpect(jsonPath("$.detailDeveloper.error",
                is("Idade ultrapassa pol??tica de idade m??nima: " + configuracoesGerais.getCliente().getIdadeMinima())));
    }

    @Test
    void validarPutClienteDataNascimentoComDesacordoPoliticaIdadeMaxima() throws Exception {

        final String nome = "Fernando de Lima";
        final String email = "fernando.dpaula@gmail.com";
        final LocalDate dataNascimento = LocalDate.of(1983, 12, 30);

        final Cliente clienteSave = repository.save(Cliente.builder()
            .nome(nome)
            .email(email)
            .dataNascimento(dataNascimento)
            .dataInclusao(LocalDateTime.now())
            .build());

        final ClienteDTO clienteDTO = ClienteDTO.builder()
            .id(clienteSave.getId())
            .nome("Fernando de Paula de Lima")
            .email("fernando.dpaula@hotmail.com")
            .dataNascimento(LocalDate.of(1878, 12, 30))
            .build();

        mockMvc.perform(MockMvcRequestBuilders.put(URI_CLIENTES)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(JsonUtilsTest.toJson(clienteDTO)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.message",
                is("Idade ultrapassa pol??tica de idade m??xima: " + configuracoesGerais.getCliente().getIdadeMaxima())))
            .andExpect(jsonPath("$.status", is(HttpStatus.UNPROCESSABLE_ENTITY.value())))
            .andExpect(jsonPath("$.url", is(URI_CLIENTES)))
            .andExpect(
                jsonPath("$.detailDeveloper.exceptionClass",
                    is("com.dpaula.clientesapi.error.UnprocessableEntityException")))
            .andExpect(jsonPath("$.detailDeveloper.error",
                is("Idade ultrapassa pol??tica de idade m??xima: " + configuracoesGerais.getCliente().getIdadeMaxima())));
    }

    @Test
    void validarPutIdNull() throws Exception {

        final String nome = "Fernando de Lima";
        final String email = "fernando.dpaula@gmail.com";
        final LocalDate dataNascimento = LocalDate.of(1983, 12, 30);

        repository.save(Cliente.builder()
            .nome(nome)
            .email(email)
            .dataNascimento(dataNascimento)
            .dataInclusao(LocalDateTime.now())
            .build());

        final ClienteDTO clienteDTO = ClienteDTO.builder()
            .nome("Fernando de Paula de Lima")
            .email("fernando.dpaula@hotmail.com")
            .dataNascimento(LocalDate.of(1993, 12, 30))
            .build();

        mockMvc.perform(MockMvcRequestBuilders.put(URI_CLIENTES)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(JsonUtilsTest.toJson(clienteDTO)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.message",
                is("Id deve ser informado para alterar Cliente!")))
            .andExpect(jsonPath("$.status", is(HttpStatus.UNPROCESSABLE_ENTITY.value())))
            .andExpect(jsonPath("$.url", is(URI_CLIENTES)))
            .andExpect(
                jsonPath("$.detailDeveloper.exceptionClass",
                    is("com.dpaula.clientesapi.error.UnprocessableEntityException")))
            .andExpect(jsonPath("$.detailDeveloper.error",
                is("Id deve ser informado para alterar Cliente!")));
    }

    @Test
    void validarGet() throws Exception {

        final Cliente clienteSalvo = criarClienteSave();

        mockMvc.perform(MockMvcRequestBuilders.get(URI_CLIENTES_ID, clienteSalvo.getId().toString())
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.nome", is(clienteSalvo.getNome())))
            .andExpect(jsonPath("$.email", is(clienteSalvo.getEmail())))
            .andExpect(jsonPath("$.dataNascimento", is(clienteSalvo.getDataNascimento().format(
                Util.DATE_FORMAT))))
            .andExpect(jsonPath("$.dataInclusao").isNotEmpty())
            .andExpect(jsonPath("$.dataAlteracao").isEmpty())
            .andExpect(jsonPath("$.idade", is((int) (getIdade(clienteSalvo.getDataNascimento())))));
    }

    @Test
    void validarGetNotFound() throws Exception {

        final UUID id = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.get(URI_CLIENTES_ID, id.toString())
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", is("N??o encontrada informa????es solicitadas!")))
            .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
            .andExpect(jsonPath("$.url", is(URI_CLIENTES + "/" + id)))
            .andExpect(
                jsonPath("$.detailDeveloper.exceptionClass",
                    is("com.dpaula.clientesapi.error.ObjectNotFoundException")))
            .andExpect(jsonPath("$.detailDeveloper.error",
                is("Objeto n??o encontrado: <id>: <" + id + "> Class: <com.dpaula.clientesapi.entity.Cliente>")));
    }

    @Test
    void validarDelete() throws Exception {

        final Cliente clienteSalvo = criarClienteSave();

        mockMvc.perform(MockMvcRequestBuilders.delete(URI_CLIENTES_ID, clienteSalvo.getId().toString())
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk());

        final Optional<Cliente> clienteOptional = repository.findById(clienteSalvo.getId());
        assertThat(clienteOptional.isEmpty()).isTrue();
    }

    @Test
    void validarDeleteNotFound() throws Exception {

        final UUID id = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.delete(URI_CLIENTES_ID, id.toString())
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", is("N??o encontrada informa????es solicitadas!")))
            .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
            .andExpect(jsonPath("$.url", is(URI_CLIENTES + "/" + id)))
            .andExpect(
                jsonPath("$.detailDeveloper.exceptionClass",
                    is("com.dpaula.clientesapi.error.ObjectNotFoundException")))
            .andExpect(jsonPath("$.detailDeveloper.error",
                is("Objeto n??o encontrado: <id>: <" + id + "> Class: <com.dpaula.clientesapi.entity.Cliente>")));
    }

    @Test
    void validarListAllTodos() throws Exception {

        final Cliente clienteSalvo = criarClienteSave();

        mockMvc.perform(MockMvcRequestBuilders.get(URI_CLIENTES)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
            .andExpect(jsonPath("$.pageable.pageSize", is(20)))
            .andExpect(jsonPath("$.pageable.offset", is(0)))
            .andExpect(jsonPath("$.pageable.paged", is(true)))
            .andExpect(jsonPath("$.pageable.unpaged", is(false)))
            .andExpect(jsonPath("$.last", is(true)))
            .andExpect(jsonPath("$.totalPages", is(1)))
            .andExpect(jsonPath("$.totalElements", is(1)))
            .andExpect(jsonPath("$.numberOfElements", is(1)))
            .andExpect(jsonPath("$.content.[0].id", is(clienteSalvo.getId().toString())))
            .andExpect(jsonPath("$.content.[0].nome", is(clienteSalvo.getNome())))
            .andExpect(jsonPath("$.content.[0].email", is(clienteSalvo.getEmail())))
            .andExpect(jsonPath("$.content.[0].dataNascimento", is(clienteSalvo.getDataNascimento().format(
                Util.DATE_FORMAT))))
            .andExpect(jsonPath("$.content.[0].idade", is((int) (getIdade(clienteSalvo.getDataNascimento())))))
            .andExpect(jsonPath("$.content.[0].dataInclusao").isNotEmpty());
    }

    @Test
    void validarListarFiltroPorNome() throws Exception {

        criarClienteSaveFlex(
            "Fernando de Lima",
            "fernando.lima@gmail.com",
            LocalDate.of(1983, 12, 30));
        final Cliente clientePaula = criarClienteSaveFlex(
            "Fernando de Paula",
            "fernando.paula@gmail.com",
            LocalDate.of(1993, 12, 30));

        mockMvc.perform(MockMvcRequestBuilders.get(URI_CLIENTES + "?nome=" + clientePaula.getNome())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
            .andExpect(jsonPath("$.pageable.pageSize", is(20)))
            .andExpect(jsonPath("$.pageable.offset", is(0)))
            .andExpect(jsonPath("$.pageable.paged", is(true)))
            .andExpect(jsonPath("$.pageable.unpaged", is(false)))
            .andExpect(jsonPath("$.last", is(true)))
            .andExpect(jsonPath("$.totalPages", is(1)))
            .andExpect(jsonPath("$.totalElements", is(1)))
            .andExpect(jsonPath("$.numberOfElements", is(1)))
            .andExpect(jsonPath("$.content.[0].id", is(clientePaula.getId().toString())))
            .andExpect(jsonPath("$.content.[0].nome", is(clientePaula.getNome())))
            .andExpect(jsonPath("$.content.[0].email", is(clientePaula.getEmail())))
            .andExpect(jsonPath("$.content.[0].dataNascimento", is(clientePaula.getDataNascimento().format(
                Util.DATE_FORMAT))))
            .andExpect(jsonPath("$.content.[0].idade", is((int) (getIdade(clientePaula.getDataNascimento())))))
            .andExpect(jsonPath("$.content.[0].dataInclusao").isNotEmpty());
    }

    @Test
    void validarListarFiltroPorEmailComUmRetorno() throws Exception {

        final Cliente clienteLima = criarClienteSaveFlex(
            "Fernando de Lima",
            "fernando.lima@gmail.com",
            LocalDate.of(1983, 12, 30));
        criarClienteSaveFlex(
            "Fernando de Paula",
            "fernando.paula@gmail.com",
            LocalDate.of(1993, 12, 30));

        mockMvc.perform(MockMvcRequestBuilders.get(URI_CLIENTES + "?email=" + clienteLima.getEmail())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
            .andExpect(jsonPath("$.pageable.pageSize", is(20)))
            .andExpect(jsonPath("$.pageable.offset", is(0)))
            .andExpect(jsonPath("$.pageable.paged", is(true)))
            .andExpect(jsonPath("$.pageable.unpaged", is(false)))
            .andExpect(jsonPath("$.last", is(true)))
            .andExpect(jsonPath("$.totalPages", is(1)))
            .andExpect(jsonPath("$.totalElements", is(1)))
            .andExpect(jsonPath("$.numberOfElements", is(1)))
            .andExpect(jsonPath("$.content.[0].id", is(clienteLima.getId().toString())))
            .andExpect(jsonPath("$.content.[0].nome", is(clienteLima.getNome())))
            .andExpect(jsonPath("$.content.[0].email", is(clienteLima.getEmail())))
            .andExpect(jsonPath("$.content.[0].dataNascimento", is(clienteLima.getDataNascimento().format(
                Util.DATE_FORMAT))))
            .andExpect(jsonPath("$.content.[0].idade", is((int) (getIdade(clienteLima.getDataNascimento())))))
            .andExpect(jsonPath("$.content.[0].dataInclusao").isNotEmpty());
    }

    @Test
    void validarListarFiltroPorIdadeComDoisRetornos() throws Exception {

        criarClienteSaveFlex(
            "Fernando de Lima",
            "fernando.lima@gmail.com",
            LocalDate.of(1983, 12, 30));
        final Cliente clientePaula = criarClienteSaveFlex(
            "Fernando de Paula",
            "fernando.paula@gmail.com",
            LocalDate.of(1993, 12, 30));
        final Cliente clienteSilva = criarClienteSaveFlex(
            "Alex da Silva",
            "alex.silva@gmail.com",
            LocalDate.of(1993, 12, 30));

        mockMvc.perform(MockMvcRequestBuilders.get(URI_CLIENTES + "?idade=27")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
            .andExpect(jsonPath("$.pageable.pageSize", is(20)))
            .andExpect(jsonPath("$.pageable.offset", is(0)))
            .andExpect(jsonPath("$.pageable.paged", is(true)))
            .andExpect(jsonPath("$.pageable.unpaged", is(false)))
            .andExpect(jsonPath("$.last", is(true)))
            .andExpect(jsonPath("$.totalPages", is(1)))
            .andExpect(jsonPath("$.totalElements", is(2)))
            .andExpect(jsonPath("$.numberOfElements", is(2)))
            .andExpect(jsonPath("$.content.[0].id", is(clienteSilva.getId().toString())))
            .andExpect(jsonPath("$.content.[0].nome", is(clienteSilva.getNome())))
            .andExpect(jsonPath("$.content.[0].email", is(clienteSilva.getEmail())))
            .andExpect(jsonPath("$.content.[0].dataNascimento", is(clienteSilva.getDataNascimento().format(
                Util.DATE_FORMAT))))
            .andExpect(jsonPath("$.content.[0].idade", is((int) (getIdade(clienteSilva.getDataNascimento())))))
            .andExpect(jsonPath("$.content.[0].dataInclusao").isNotEmpty())
            .andExpect(jsonPath("$.content.[1].id", is(clientePaula.getId().toString())))
            .andExpect(jsonPath("$.content.[1].nome", is(clientePaula.getNome())))
            .andExpect(jsonPath("$.content.[1].email", is(clientePaula.getEmail())))
            .andExpect(jsonPath("$.content.[1].dataNascimento", is(clientePaula.getDataNascimento().format(
                Util.DATE_FORMAT))))
            .andExpect(jsonPath("$.content.[1].idade", is((int) (getIdade(clientePaula.getDataNascimento())))))
            .andExpect(jsonPath("$.content.[1].dataInclusao").isNotEmpty());
    }

    @Test
    void validarListarFiltroPorDataNascimentoComDoisRetornos() throws Exception {

        criarClienteSaveFlex(
            "Fernando de Lima",
            "fernando.lima@gmail.com",
            LocalDate.of(1983, 12, 30));
        final Cliente clientePaula = criarClienteSaveFlex(
            "Fernando de Paula",
            "fernando.paula@gmail.com",
            LocalDate.of(1993, 12, 30));
        final Cliente clienteSilva = criarClienteSaveFlex(
            "Alex da Silva",
            "alex.silva@gmail.com",
            LocalDate.of(1993, 12, 30));

        mockMvc.perform(MockMvcRequestBuilders.get(URI_CLIENTES + "?data-nascimento=1993-12-30")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
            .andExpect(jsonPath("$.pageable.pageSize", is(20)))
            .andExpect(jsonPath("$.pageable.offset", is(0)))
            .andExpect(jsonPath("$.pageable.paged", is(true)))
            .andExpect(jsonPath("$.pageable.unpaged", is(false)))
            .andExpect(jsonPath("$.last", is(true)))
            .andExpect(jsonPath("$.totalPages", is(1)))
            .andExpect(jsonPath("$.totalElements", is(2)))
            .andExpect(jsonPath("$.numberOfElements", is(2)))
            .andExpect(jsonPath("$.content.[0].id", is(clienteSilva.getId().toString())))
            .andExpect(jsonPath("$.content.[0].nome", is(clienteSilva.getNome())))
            .andExpect(jsonPath("$.content.[0].email", is(clienteSilva.getEmail())))
            .andExpect(jsonPath("$.content.[0].dataNascimento", is(clienteSilva.getDataNascimento().format(
                Util.DATE_FORMAT))))
            .andExpect(jsonPath("$.content.[0].idade", is((int) (getIdade(clienteSilva.getDataNascimento())))))
            .andExpect(jsonPath("$.content.[0].dataInclusao").isNotEmpty())
            .andExpect(jsonPath("$.content.[1].id", is(clientePaula.getId().toString())))
            .andExpect(jsonPath("$.content.[1].nome", is(clientePaula.getNome())))
            .andExpect(jsonPath("$.content.[1].email", is(clientePaula.getEmail())))
            .andExpect(jsonPath("$.content.[1].dataNascimento", is(clientePaula.getDataNascimento().format(
                Util.DATE_FORMAT))))
            .andExpect(jsonPath("$.content.[1].idade", is((int) (getIdade(clientePaula.getDataNascimento())))))
            .andExpect(jsonPath("$.content.[1].dataInclusao").isNotEmpty());
    }

    @Test
    void validarListarFiltroPorPrimeiroNomeEDataNascimentoComUmRetorno() throws Exception {

        criarClienteSaveFlex(
            "Fernando de Lima",
            "fernando.lima@gmail.com",
            LocalDate.of(1983, 12, 30));
        final Cliente clientePaula = criarClienteSaveFlex(
            "Fernando de Paula",
            "fernando.paula@gmail.com",
            LocalDate.of(1993, 12, 30));
        criarClienteSaveFlex(
            "Alex da Silva",
            "alex.silva@gmail.com",
            LocalDate.of(1993, 12, 30));

        mockMvc.perform(MockMvcRequestBuilders.get(URI_CLIENTES + "?nome=fernando&data-nascimento=1993-12-30")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
            .andExpect(jsonPath("$.pageable.pageSize", is(20)))
            .andExpect(jsonPath("$.pageable.offset", is(0)))
            .andExpect(jsonPath("$.pageable.paged", is(true)))
            .andExpect(jsonPath("$.pageable.unpaged", is(false)))
            .andExpect(jsonPath("$.last", is(true)))
            .andExpect(jsonPath("$.totalPages", is(1)))
            .andExpect(jsonPath("$.totalElements", is(1)))
            .andExpect(jsonPath("$.numberOfElements", is(1)))
            .andExpect(jsonPath("$.content.[0].id", is(clientePaula.getId().toString())))
            .andExpect(jsonPath("$.content.[0].nome", is(clientePaula.getNome())))
            .andExpect(jsonPath("$.content.[0].email", is(clientePaula.getEmail())))
            .andExpect(jsonPath("$.content.[0].dataNascimento", is(clientePaula.getDataNascimento().format(
                Util.DATE_FORMAT))))
            .andExpect(jsonPath("$.content.[0].idade", is((int) (getIdade(clientePaula.getDataNascimento())))))
            .andExpect(jsonPath("$.content.[0].dataInclusao").isNotEmpty());
    }

    private long getIdade(final LocalDate dataNascimento) {
        return YEARS.between(dataNascimento, LocalDate.now());
    }

    private Cliente criarClienteSave() {

        final Cliente clienteBase = Cliente.builder()
            .nome("Fernando de Lima")
            .email("fernando.dpaula@gmail.com")
            .dataNascimento(LocalDate.of(1983, 12, 30))
            .dataInclusao(LocalDateTime.now())
            .build();

        return repository.save(clienteBase);
    }

    private Cliente criarClienteSaveFlex(final String nome, final String email, final LocalDate dataNascimento) {

        final Cliente clienteBase = Cliente.builder()
            .nome(nome)
            .email(email)
            .dataNascimento(dataNascimento)
            .dataInclusao(LocalDateTime.now())
            .build();

        return repository.save(clienteBase);
    }

    private ClienteDTO criarClienteDTO() {
        return ClienteDTO.builder()
            .nome("Fernando de Lima")
            .email("fernando.dpaula@gmail.com")
            .dataNascimento(LocalDate.of(1983, 12, 30))
            .build();
    }
}
