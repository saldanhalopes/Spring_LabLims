package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.AnaliseTipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AnaliseTipoRepository extends JpaRepository<AnaliseTipo, Integer> {

    Page<AnaliseTipo> findAllById(Integer id, Pageable pageable);

}
