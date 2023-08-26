package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.AmostraTipo;
import br.com.lablims.spring_lablims.domain.Lote;
import br.com.lablims.spring_lablims.domain.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LoteRepository extends JpaRepository<Lote, Integer> {

    Page<Lote> findAllById(Integer id, Pageable pageable);

    Lote findFirstByMaterial(Material material);

    Lote findFirstByAmostraTipo(AmostraTipo amostraTipo);

}
