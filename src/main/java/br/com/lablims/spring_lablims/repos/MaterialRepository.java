package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.AmostraTipo;
import br.com.lablims.spring_lablims.domain.Categoria;
import br.com.lablims.spring_lablims.domain.Material;
import br.com.lablims.spring_lablims.domain.StorageEndereco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface MaterialRepository extends JpaRepository<Material, Integer> {

    Page<Material> findAllById(Integer id, Pageable pageable);

    @Query("Select mat FROM Material mat " +
            "LEFT JOIN FETCH mat.fornecedor forn " +
            "LEFT JOIN FETCH mat.unidade unt " +
            "LEFT JOIN FETCH mat.categoria cat " +
            "LEFT JOIN FETCH mat.fabricante fabr " +
            "WHERE mat.id = :id")
    Optional<Material> findMaterialById(@Param("id") Integer id);

    @Query("Select mat FROM Material mat " +
            "LEFT JOIN FETCH mat.fornecedor forn " +
            "LEFT JOIN FETCH mat.unidade unt " +
            "LEFT JOIN FETCH mat.categoria cat " +
            "LEFT JOIN FETCH mat.fabricante fabr ")
    Page<Material> findAllOfMateriais(Pageable pageable);

    @Query("Select mat FROM Material mat " +
            "LEFT JOIN FETCH mat.fornecedor forn " +
            "LEFT JOIN FETCH mat.unidade unt " +
            "LEFT JOIN FETCH mat.categoria cat " +
            "LEFT JOIN FETCH mat.fabricante fabr ")
    List<Material> findListOfMateriais(Sort sort);

    @Query("Select mat FROM Material mat " +
            "LEFT JOIN FETCH mat.fornecedor forn " +
            "LEFT JOIN FETCH mat.unidade unt " +
            "LEFT JOIN FETCH mat.categoria cat " +
            "LEFT JOIN FETCH mat.fabricante fabr " +
            "WHERE CONCAT(mat.id, ' ', mat.ativo, ' ', mat.codigo, ' ', mat.material, ' ',cat.categoria,  ' ',forn.fornecedor) LIKE %?1%")
    Page<Material> findAllByKeyword(String keyword, Pageable pageable);

    Material findFirstByCategoria(Categoria categoria);
}
