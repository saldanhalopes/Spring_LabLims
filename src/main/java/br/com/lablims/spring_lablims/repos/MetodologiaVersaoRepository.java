package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.domain.MetodologiaVersao;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MetodologiaVersaoRepository extends JpaRepository<MetodologiaVersao, Integer> {

    Page<MetodologiaVersao> findAllById(Integer id, Pageable pageable);

    List<MetodologiaVersao> findAllByProduto(Produto produto);

    MetodologiaVersao findFirstByProduto(Produto produto);

    MetodologiaVersao findFirstByMetodologia(Metodologia metodologia);

    MetodologiaVersao findFirstByAnexo(Arquivos arquivos);

    MetodologiaVersao findFirstByStatus(MetodologiaStatus metodologiaStatus);

}
