package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Analise;
import br.com.lablims.spring_lablims.domain.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface AnaliseRepository extends JpaRepository<Analise, Integer> {

    Page<Analise> findAllById(Integer id, Pageable pageable);

    @Query("Select anl FROM Analise anl " +
            "INNER JOIN FETCH anl.analiseTipo tipo ")
    Page<Analise> findAllOfAnalise(Pageable pageable);

    @Query("Select anl FROM Analise anl " +
            "INNER JOIN FETCH anl.analiseTipo tipo " +
            "WHERE CONCAT(anl.id, ' ', anl.analise, ' ', anl.siglaAnalise, ' ', tipo.analiseTipo) LIKE %?1%")
    Page<Analise> findAllByKeyword(String keyword, Pageable pageable);

    @Query("Select anl FROM Analise anl " +
            "INNER JOIN FETCH anl.analiseTipo tipo " +
            "WHERE anl.id = :id")
    Optional<Analise> findAnaliseById(@Param("id") Integer id);


}
