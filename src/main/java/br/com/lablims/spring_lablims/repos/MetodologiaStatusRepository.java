package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.MetodologiaStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MetodologiaStatusRepository extends JpaRepository<MetodologiaStatus, Integer> {

    Page<MetodologiaStatus> findAllById(Integer id, Pageable pageable);

}
