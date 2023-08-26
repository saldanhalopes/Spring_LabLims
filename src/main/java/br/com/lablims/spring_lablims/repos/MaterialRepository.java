package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Material;
import br.com.lablims.spring_lablims.domain.MaterialTipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MaterialRepository extends JpaRepository<Material, Integer> {

    Page<Material> findAllById(Integer id, Pageable pageable);

    Material findFirstByTipoMaterial(MaterialTipo materialTipo);

}
