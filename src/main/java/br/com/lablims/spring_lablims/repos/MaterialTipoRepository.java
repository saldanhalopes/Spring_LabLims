package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.MaterialTipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MaterialTipoRepository extends JpaRepository<MaterialTipo, Integer> {

    Page<MaterialTipo> findAllById(Integer id, Pageable pageable);

}
