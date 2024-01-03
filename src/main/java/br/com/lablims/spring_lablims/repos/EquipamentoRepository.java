package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface EquipamentoRepository extends JpaRepository<Equipamento, Integer> {

    Page<Equipamento> findAllById(Integer id, Pageable pageable);

    @Transactional
    @Query("Select eq FROM Equipamento eq LEFT JOIN eq.tipo tp LEFT JOIN eq.setor st LEFT JOIN eq.grandeza esc LEFT JOIN eq.fabricante fab")
    Page<Equipamento> findAllOfEquipamentos(Pageable pageable);
    @Transactional
    @Query("Select eq FROM Equipamento eq INNER JOIN eq.tipo tp INNER JOIN eq.setor st WHERE tp.tipo = :tipoEq AND st.id = :setorID ORDER BY tp.tipo ASC")
    Page<Equipamento> findTagByTipoSetor(@Param("tipoEq") String tipoEq, @Param("setorID") Integer setorID,Pageable pageable);
    @Transactional
    @Query("Select eq FROM Equipamento eq LEFT JOIN FETCH eq.arquivos arq WHERE eq.id = :eqId")
    Equipamento findArquivosByEquipamento(@Param("eqId") Integer eqId);

    Equipamento findFirstBySetor(Setor setor);

    Equipamento findFirstByGrandeza(Grandeza grandeza);

    Equipamento findFirstByTipo(EquipamentoTipo equipamentoTipo);

    List<Equipamento> findAllByArquivos(Arquivos arquivos);

}
