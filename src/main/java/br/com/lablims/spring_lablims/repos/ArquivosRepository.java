package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ArquivosRepository extends JpaRepository<Arquivos, Integer> {

    Page<Arquivos> findAllById(Integer id, Pageable pageable);

}
