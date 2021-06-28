package com.dpaula.clientesapi.endpoint;

import com.dpaula.clientesapi.vo.ClienteVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    ResponseEntity<Page<ClienteVO>> listAll(
        @PageableDefault(sort = "dataInclusao", direction = Sort.Direction.DESC) @Parameter(hidden = true) Pageable pageable);
}
