package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.ColunaStorage;
import br.com.lablims.spring_lablims.domain.ColunaVaga;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ColunaVagaRepository extends JpaRepository<ColunaVaga, Integer> {

    Page<ColunaVaga> findAllById(Integer id, Pageable pageable);

    ColunaVaga findFirstByColunaStorage(ColunaStorage colunaStorage);

}
