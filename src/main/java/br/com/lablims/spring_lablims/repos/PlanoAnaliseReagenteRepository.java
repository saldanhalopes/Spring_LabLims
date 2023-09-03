package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.PlanoAnaliseReagente;
import br.com.lablims.spring_lablims.domain.Reagente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlanoAnaliseReagenteRepository extends JpaRepository<PlanoAnaliseReagente, Integer> {

    Page<PlanoAnaliseReagente> findAllById(Integer id, Pageable pageable);

    PlanoAnaliseReagente findFirstByReagente(Reagente reagente);

}
