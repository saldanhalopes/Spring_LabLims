package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Arquivos;
import br.com.lablims.spring_lablims.domain.Material;
import br.com.lablims.spring_lablims.domain.Metodologia;
import br.com.lablims.spring_lablims.domain.MetodologiaStatus;
import br.com.lablims.spring_lablims.domain.MetodologiaVesao;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MetodologiaVesaoRepository extends JpaRepository<MetodologiaVesao, Integer> {

    Page<MetodologiaVesao> findAllById(Integer id, Pageable pageable);

    List<MetodologiaVesao> findAllByMaterial(Material material);

    MetodologiaVesao findFirstByMaterial(Material material);

    MetodologiaVesao findFirstByMetodologia(Metodologia metodologia);

    MetodologiaVesao findFirstByAnexo(Arquivos arquivos);

    MetodologiaVesao findFirstByStatus(MetodologiaStatus metodologiaStatus);

}
