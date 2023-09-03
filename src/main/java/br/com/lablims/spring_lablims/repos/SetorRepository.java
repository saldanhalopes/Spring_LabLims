package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Departamento;
import br.com.lablims.spring_lablims.domain.Setor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SetorRepository extends JpaRepository<Setor, Integer> {

    Page<Setor> findAllById(Integer id, Pageable pageable);

    Setor findFirstByDepartamento(Departamento departamento);

}
