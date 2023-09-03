package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.AnaliseProdutividade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AnaliseProdutividadeRepository extends JpaRepository<AnaliseProdutividade, Integer> {

    Page<AnaliseProdutividade> findAllById(Integer id, Pageable pageable);

}
