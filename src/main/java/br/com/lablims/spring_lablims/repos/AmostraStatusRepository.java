package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.domain.AmostraStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AmostraStatusRepository extends JpaRepository<AmostraStatus, Integer> {

    Page<AmostraStatus> findAllById(Integer id, Pageable pageable);

    AmostraStatus findFirstByConferente1(Usuario usuario);

    AmostraStatus findFirstByConferente2(Usuario usuario);

    AmostraStatus findFirstByAnaliseStatus(AnaliseStatus analiseStatus);

    AmostraStatus findFirstByPlanoAnalise(PlanoAnalise planoAnalise);

    AmostraStatus findFirstByAmostra(Amostra amostra);

}
