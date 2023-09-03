package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Coluna;
import br.com.lablims.spring_lablims.domain.PlanoAnalise;
import br.com.lablims.spring_lablims.domain.PlanoAnaliseColuna;
import br.com.lablims.spring_lablims.domain.UnidadeMedida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlanoAnaliseColunaRepository extends JpaRepository<PlanoAnaliseColuna, Integer> {

    Page<PlanoAnaliseColuna> findAllById(Integer id, Pageable pageable);

    PlanoAnaliseColuna findFirstByUnidade(UnidadeMedida unidadeMedida);

    PlanoAnaliseColuna findFirstByColuna(Coluna coluna);

    PlanoAnaliseColuna findFirstByPlanoAnalise(PlanoAnalise planoAnalise);

}
