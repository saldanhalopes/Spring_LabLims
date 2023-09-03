package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Coluna;
import br.com.lablims.spring_lablims.domain.ColunaConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ColunaRepository extends JpaRepository<Coluna, Integer> {

    Page<Coluna> findAllById(Integer id, Pageable pageable);

    Coluna findFirstByTipoColuna(ColunaConfig colunaConfig);

    Coluna findFirstByFabricanteColuna(ColunaConfig colunaConfig);

    Coluna findFirstByMarcaColuna(ColunaConfig colunaConfig);

    Coluna findFirstByFaseColuna(ColunaConfig colunaConfig);

    Coluna findFirstByTamanhoColuna(ColunaConfig colunaConfig);

    Coluna findFirstByDiametroColuna(ColunaConfig colunaConfig);

    Coluna findFirstByParticulaColuna(ColunaConfig colunaConfig);

}
