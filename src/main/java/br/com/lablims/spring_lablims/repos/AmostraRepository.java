package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface AmostraRepository extends JpaRepository<Amostra, Integer> {

    Page<Amostra> findAllById(Integer id, Pageable pageable);

    @Query("Select am FROM Amostra am LEFT JOIN FETCH am.lote lot LEFT JOIN FETCH am.unidade unt " +
            "LEFT JOIN FETCH am.amostraTipo amt LEFT JOIN FETCH am.usuarioResponsavel usr" +
            "LEFT JOIN FETCH lot.produto mat")
    Page<Amostra> findAllOfAmostras(Pageable pageable);

    @Query("Select am FROM Amostra am WHERE am.id = :id")
    Optional<Amostra> findAmostra(@Param("id") Integer id);

    Amostra findFirstByUnidade(UnidadeMedida unidadeMedida);

    Amostra findFirstByUsuarioResponsavel(Usuario usuario);

    Amostra findFirstByCodigoAmostra(String codigoAmostra);
    Amostra findFirstByLote(Lote lote);

    Amostra findFirstByAmostraTipo(AmostraTipo amostraTipo);

    boolean existsByCodigoAmostraIgnoreCase(String amostra);

    List<Amostra> findAllByArquivos(Arquivos arquivos);

}
