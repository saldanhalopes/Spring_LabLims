package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Fabricante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FabricanteRepository extends JpaRepository<Fabricante, Integer> {

    Page<Fabricante> findAllById(Integer id, Pageable pageable);

}
