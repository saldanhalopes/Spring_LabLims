package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.ColunaConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ColunaConfigRepository extends JpaRepository<ColunaConfig, Integer> {

    Page<ColunaConfig> findAllById(Integer id, Pageable pageable);

}
