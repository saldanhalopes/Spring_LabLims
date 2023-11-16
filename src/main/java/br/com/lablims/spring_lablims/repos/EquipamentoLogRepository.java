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


public interface EquipamentoLogRepository extends JpaRepository<EquipamentoLog, Integer> {

    Page<EquipamentoLog> findAllById(Integer id, Pageable pageable);

    Page<EquipamentoLog> findAllByEquipamento_Id(Integer equipamento_Id, Pageable pageable);

    EquipamentoLog findFirstByUsuarioInicio(Usuario usuario);

    EquipamentoLog findFirstByUsuarioFim(Usuario usuario);

    EquipamentoLog findFirstByEquipamento(Equipamento equipamento);

    EquipamentoLog findFirstByAtividade(EquipamentoAtividade equipamentoAtividade);

    @Transactional
    @Query("Select eqLog FROM EquipamentoLog eqLog LEFT JOIN FETCH eqLog.arquivos arq WHERE eqLog.id = :id")
    EquipamentoLog findEquipamentoLogWithArquivos(@Param("id") Integer id);

    @Transactional
    @Query("Select eqLog FROM EquipamentoLog eqLog LEFT JOIN FETCH eqLog.amostra amt WHERE eqLog.id = :id")
    EquipamentoLog findEquipamentoLogWithAmostras(@Param("id") Integer id);

    List<EquipamentoLog> findAllByArquivos(Arquivos arquivos);

    List<EquipamentoLog> findAllByAmostra(Optional<Amostra> amostra);


}
