package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Page<Cliente> findAllById(Integer id, Pageable pageable);

}
