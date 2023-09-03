package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Analise;
import br.com.lablims.spring_lablims.domain.AnaliseTipo;
import br.com.lablims.spring_lablims.domain.MetodologiaVesao;
import br.com.lablims.spring_lablims.domain.PlanoAnalise;
import br.com.lablims.spring_lablims.domain.Setor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlanoAnaliseRepository extends JpaRepository<PlanoAnalise, Integer> {

    Page<PlanoAnalise> findAllById(Integer id, Pageable pageable);

    PlanoAnalise findFirstBySetor(Setor setor);

    PlanoAnalise findFirstByMetodologiaVersao(MetodologiaVesao metodologiaVesao);

    PlanoAnalise findFirstByAnalise(Analise analise);

    PlanoAnalise findFirstByAnaliseTipo(AnaliseTipo analiseTipo);

}
