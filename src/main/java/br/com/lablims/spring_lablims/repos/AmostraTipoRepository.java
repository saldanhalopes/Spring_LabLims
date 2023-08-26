package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.AmostraTipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AmostraTipoRepository extends JpaRepository<AmostraTipo, Integer> {

    Page<AmostraTipo> findAllById(Integer id, Pageable pageable);

}
