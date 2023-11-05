package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface LoteRepository extends JpaRepository<Lote, Integer> {

    Page<Lote> findAllById(Integer id, Pageable pageable);

    @Query("Select lot FROM Lote lot LEFT JOIN lot.unidade unt LEFT JOIN lot.material mat LEFT JOIN lot.cliente cli")
    Page<Lote> findAllOfLotes(Pageable pageable);
    Lote findFirstByUnidade(UnidadeMedida unidade);
    Lote findFirstByMaterial(Material material);
    Lote findFirstByCliente(Cliente cliente);
    @Query("Select lote FROM Lote lote LEFT JOIN FETCH lote.arquivos arq WHERE lote.id = :loteId")
    Lote findArquivosByLote(@Param("loteId") Integer loteId);

    List<Lote> findAllByArquivos(Arquivos arquivos);
    boolean existsByLoteIgnoreCase(String lote);
}
