package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.ColunaStorageTipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ColunaStorageTipoRepository extends JpaRepository<ColunaStorageTipo, Integer> {

    Page<ColunaStorageTipo> findAllById(Integer id, Pageable pageable);

}
