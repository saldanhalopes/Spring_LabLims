package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Estoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {

    Page<Estoque> findAllById(Integer id, Pageable pageable);

    @Query("Select est FROM Estoque est " +
            "INNER JOIN FETCH est.material mat " +
            "LEFT JOIN FETCH est.setor setor")
    Page<Estoque> findAllOfEstoque(Pageable pageable);

}
