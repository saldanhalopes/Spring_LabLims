package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.ProdutoTipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProdutoTipoRepository extends JpaRepository<ProdutoTipo, Integer> {

    Page<ProdutoTipo> findAllById(Integer id, Pageable pageable);

}
