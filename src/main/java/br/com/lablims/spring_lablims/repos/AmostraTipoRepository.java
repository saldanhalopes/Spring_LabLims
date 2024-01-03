package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.AmostraTipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface AmostraTipoRepository extends JpaRepository<AmostraTipo, Integer> {

    Page<AmostraTipo> findAllById(Integer id, Pageable pageable);
    @Query("SELECT at FROM AmostraTipo at WHERE CONCAT(at.id, ' ', at.tipo, ' ', at.version) LIKE %?1%")
    Page<AmostraTipo> findAllByKeyword(String keyword, Pageable pageable);

}
