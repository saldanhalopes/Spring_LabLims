package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Fornecedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FornecedorRepository extends JpaRepository<Fornecedor, Integer> {

    Page<Fornecedor> findAllById(Integer id, Pageable pageable);

}
