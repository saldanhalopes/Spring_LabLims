package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Arquivos;
import br.com.lablims.spring_lablims.domain.Equipamento;
import br.com.lablims.spring_lablims.domain.EquipamentoTipo;
import br.com.lablims.spring_lablims.domain.EscalaMedida;
import br.com.lablims.spring_lablims.domain.Setor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EquipamentoRepository extends JpaRepository<Equipamento, Integer> {

    Page<Equipamento> findAllById(Integer id, Pageable pageable);

    Equipamento findFirstBySetor(Setor setor);

    Equipamento findFirstByEscala(EscalaMedida escalaMedida);

    Equipamento findFirstByCertificado(Arquivos arquivos);

    Equipamento findFirstByManual(Arquivos arquivos);

    Equipamento findFirstByProcedimento(Arquivos arquivos);

    Equipamento findFirstByTipo(EquipamentoTipo equipamentoTipo);

}
