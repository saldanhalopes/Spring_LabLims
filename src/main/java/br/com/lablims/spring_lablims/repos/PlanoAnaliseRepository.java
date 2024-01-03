package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlanoAnaliseRepository extends JpaRepository<PlanoAnalise, Integer> {

    Page<PlanoAnalise> findAllById(Integer id, Pageable pageable);

    PlanoAnalise findFirstBySetor(Setor setor);

    PlanoAnalise findFirstByMetodologiaVersao(MetodologiaVersao metodologiaVersao);

    PlanoAnalise findFirstByAnalise(Analise analise);

    PlanoAnalise findFirstByAnaliseTecnica(AnaliseTecnica analiseTecnica);

}
