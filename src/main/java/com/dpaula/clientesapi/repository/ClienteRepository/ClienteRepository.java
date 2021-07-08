package com.dpaula.clientesapi.repository.ClienteRepository;

import com.dpaula.clientesapi.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * @author Fernando de Lima on 28/06/21
 */
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
    boolean existsByEmail(String email);
}
