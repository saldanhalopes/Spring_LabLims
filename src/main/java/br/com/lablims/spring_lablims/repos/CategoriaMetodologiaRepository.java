package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.CategoriaMetodologia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoriaMetodologiaRepository extends JpaRepository<CategoriaMetodologia, Integer> {

    Page<CategoriaMetodologia> findAllById(Integer id, Pageable pageable);

}
