package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    Page<Categoria> findAllById(Integer id, Pageable pageable);

}
