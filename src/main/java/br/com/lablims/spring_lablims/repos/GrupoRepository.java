package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Grupo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GrupoRepository extends JpaRepository<Grupo, Integer> {

    Grupo findTopByRegra(String regra);

    Page<Grupo> findAllById(Integer id, Pageable pageable);

}
