package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Material;
import br.com.lablims.spring_lablims.domain.MaterialTipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface MaterialRepository extends JpaRepository<Material, Integer> {

    Page<Material> findAllById(Integer id, Pageable pageable);

    @Query("Select mat FROM Material mat " +
            "LEFT JOIN FETCH mat.fornecedor forn " +
            "LEFT JOIN FETCH mat.unidade unt " +
            "LEFT JOIN FETCH mat.materialTipo mtipo")
    Page<Material> findAllOfMateriais(Pageable pageable);

    Material findFirstByMaterialTipo(MaterialTipo materialTipo);
}
