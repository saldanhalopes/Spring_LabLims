package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Analise;
import br.com.lablims.spring_lablims.domain.Arquivos;
import br.com.lablims.spring_lablims.domain.Campanha;
import br.com.lablims.spring_lablims.domain.ColunaLog;
import br.com.lablims.spring_lablims.domain.ColunaUtil;
import br.com.lablims.spring_lablims.domain.Equipamento;
import br.com.lablims.spring_lablims.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ColunaLogRepository extends JpaRepository<ColunaLog, Integer> {

    Page<ColunaLog> findAllById(Integer id, Pageable pageable);

    ColunaLog findFirstByUsuarioInicio(Usuario usuario);

    ColunaLog findFirstByUsuarioFim(Usuario usuario);

    ColunaLog findFirstByAnexo(Arquivos arquivos);

    ColunaLog findFirstByAnalise(Analise analise);

    ColunaLog findFirstByColunaUtil(ColunaUtil colunaUtil);

    ColunaLog findFirstByCampanha(Campanha campanha);

    ColunaLog findFirstByEquipamento(Equipamento equipamento);

}
