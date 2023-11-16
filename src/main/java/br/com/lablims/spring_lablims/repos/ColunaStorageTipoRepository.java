package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.StorageTipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ColunaStorageTipoRepository extends JpaRepository<StorageTipo, Integer> {

    Page<StorageTipo> findAllById(Integer id, Pageable pageable);

}
