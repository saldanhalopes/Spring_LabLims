package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.EscalaMedida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EscalaMedidaRepository extends JpaRepository<EscalaMedida, Integer> {

    Page<EscalaMedida> findAllById(Integer id, Pageable pageable);

}
