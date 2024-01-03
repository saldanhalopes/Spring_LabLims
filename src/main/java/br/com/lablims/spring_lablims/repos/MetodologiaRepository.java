package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Analise;
import br.com.lablims.spring_lablims.domain.CategoriaMetodologia;
import br.com.lablims.spring_lablims.domain.Metodologia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface MetodologiaRepository extends JpaRepository<Metodologia, Integer> {

    Page<Metodologia> findAllById(Integer id, Pageable pageable);

    @Query("Select mtd FROM Metodologia mtd " +
            "LEFT JOIN FETCH mtd.categoriaMetodologia cod ")
    Page<Metodologia> findAllOfMetodologia(Pageable pageable);

    @Query("Select mtd FROM Metodologia mtd " +
            "LEFT JOIN FETCH mtd.categoriaMetodologia cod " +
            "WHERE CONCAT(mtd.id, ' ', mtd.metodo, ' ', mtd.codigo, ' ', cod.categoria) LIKE %?1%")
    Page<Metodologia> findAllByKeyword(String keyword, Pageable pageable);

    @Query("Select mtd FROM Metodologia mtd " +
            "LEFT JOIN FETCH mtd.categoriaMetodologia cod " +
            "WHERE mtd.id = :id")
    Optional<Metodologia> findMetodologiaById(@Param("id") Integer id);

    Metodologia findFirstByCategoriaMetodologia(CategoriaMetodologia categoriaMetodologia);

}
