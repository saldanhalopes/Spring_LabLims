package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Arquivos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ArquivosRepository extends JpaRepository<Arquivos, Integer> {

    Page<Arquivos> findAllById(Integer id, Pageable pageable);

}
