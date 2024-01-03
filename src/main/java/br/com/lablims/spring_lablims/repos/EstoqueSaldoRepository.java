package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.EstoqueSaldo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface EstoqueSaldoRepository extends JpaRepository<EstoqueSaldo, Integer> {

    Page<EstoqueSaldo> findAllById(Integer id, Pageable pageable);

    @Query("Select est FROM EstoqueSaldo est " +
            "INNER JOIN FETCH est.material mat " +
            "WHERE mat.id = :id")
    EstoqueSaldo findWithMaterial(@Param("id") Integer id);

    @Query("Select est FROM EstoqueSaldo est " +
            "INNER JOIN FETCH est.material mat " +
            "LEFT JOIN FETCH est.material.categoria cat " +
            "LEFT JOIN FETCH est.material.fornecedor forn " +
            "LEFT JOIN FETCH est.setor setor " +
            "LEFT JOIN FETCH est.material.unidade unit ")
    Page<EstoqueSaldo> findAllOfEstoqueSaldo(Pageable pageable);

    @Query("Select est FROM EstoqueSaldo est " +
            "INNER JOIN FETCH est.material mat " +
            "LEFT JOIN FETCH est.material.categoria cat " +
            "LEFT JOIN FETCH est.material.fornecedor forn " +
            "LEFT JOIN FETCH est.setor setor " +
            "LEFT JOIN FETCH est.material.unidade unit " +
            "WHERE est.id = :id")
    Optional<EstoqueSaldo> findEstoqueSaldoById(@Param("id") Integer id);

    @Query("Select est FROM EstoqueSaldo est " +
            "INNER JOIN FETCH est.material mat " +
            "LEFT JOIN FETCH est.material.categoria cat " +
            "LEFT JOIN FETCH est.material.fornecedor forn " +
            "LEFT JOIN FETCH est.setor setor " +
            "LEFT JOIN FETCH est.material.unidade unit " +
            "WHERE CONCAT(est.id, ' ', mat.material, ' ', cat.categoria, ' ', setor.setor, ' ',forn.fornecedor) LIKE %?1%")
    Page<EstoqueSaldo> findAllByKeyword(String keyword, Pageable pageable);

}
