package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.AnaliseTecnica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AnaliseTecnicaRepository extends JpaRepository<AnaliseTecnica, Integer> {

    Page<AnaliseTecnica> findAllById(Integer id, Pageable pageable);

}
