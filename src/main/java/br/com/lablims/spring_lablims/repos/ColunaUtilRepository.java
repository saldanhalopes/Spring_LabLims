package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Analise;
import br.com.lablims.spring_lablims.domain.Arquivos;
import br.com.lablims.spring_lablims.domain.Coluna;
import br.com.lablims.spring_lablims.domain.ColunaUtil;
import br.com.lablims.spring_lablims.domain.StorageEndereco;
import br.com.lablims.spring_lablims.domain.MetodologiaVersao;
import br.com.lablims.spring_lablims.domain.Setor;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ColunaUtilRepository extends JpaRepository<ColunaUtil, Integer> {

    Page<ColunaUtil> findAllById(Integer id, Pageable pageable);

    ColunaUtil findFirstBySetor(Setor setor);

    ColunaUtil findFirstByMetodologiaVersao(MetodologiaVersao metodologiaVersao);

    List<ColunaUtil> findAllByAnexos(Arquivos arquivos);

    ColunaUtil findFirstByCertificado(Arquivos arquivos);

    ColunaUtil findFirstByAnexos(Arquivos arquivos);

    ColunaUtil findFirstByAnalise(Analise analise);

    ColunaUtil findFirstByStorageEndereco(StorageEndereco storageEndereco);

    ColunaUtil findFirstByColuna(Coluna coluna);

}
