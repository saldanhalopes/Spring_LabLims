package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Reagente;
import br.com.lablims.spring_lablims.domain.SolucaoReagente;
import br.com.lablims.spring_lablims.domain.SolucaoRegistro;
import br.com.lablims.spring_lablims.domain.UnidadeMedida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SolucaoReagenteRepository extends JpaRepository<SolucaoReagente, Integer> {

    Page<SolucaoReagente> findAllById(Integer id, Pageable pageable);

    SolucaoReagente findFirstByUnidade(UnidadeMedida unidadeMedida);

    SolucaoReagente findFirstByReagente(Reagente reagente);

    SolucaoReagente findFirstBySolucaoRegistro(SolucaoRegistro solucaoRegistro);

}
