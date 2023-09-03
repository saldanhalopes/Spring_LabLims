package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Campanha;
import br.com.lablims.spring_lablims.domain.Celula;
import br.com.lablims.spring_lablims.domain.Lote;
import br.com.lablims.spring_lablims.domain.Setor;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CampanhaRepository extends JpaRepository<Campanha, Integer> {

    Page<Campanha> findAllById(Integer id, Pageable pageable);

    Campanha findFirstBySetor(Setor setor);

    Campanha findFirstByCelula(Celula celula);

    List<Campanha> findAllByLotes(Lote lote);

    Campanha findFirstByLotes(Lote lote);

}
