package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.*;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CampanhaRepository extends JpaRepository<Campanha, Integer> {

    Page<Campanha> findAllById(Integer id, Pageable pageable);

    Campanha findFirstBySetor(Setor setor);

    Campanha findFirstByCelula(Celula celula);

    List<Campanha> findAllByAmostras(Amostra amostra);

    Campanha findFirstByAmostras(Amostra amostra);

}
