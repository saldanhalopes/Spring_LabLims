package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Grandeza;
import br.com.lablims.spring_lablims.domain.UnidadeMedida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UnidadeMedidaRepository extends JpaRepository<UnidadeMedida, Integer> {

    Page<UnidadeMedida> findAllById(Integer id, Pageable pageable);

    UnidadeMedida findFirstByGrandeza(Grandeza grandeza);

}
