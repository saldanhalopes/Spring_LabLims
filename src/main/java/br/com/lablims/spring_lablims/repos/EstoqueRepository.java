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


public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {

    Page<Estoque> findAllById(Integer id, Pageable pageable);

    @Query("Select est FROM Estoque est " +
            "INNER JOIN FETCH est.material mat " +
            "LEFT JOIN FETCH est.material.categoria cat " +
            "LEFT JOIN FETCH est.material.fornecedor forn " +
            "LEFT JOIN FETCH est.setor setor " +
            "LEFT JOIN FETCH est.storageEndereco storage " +
            "LEFT JOIN FETCH est.material.unidade unit ")
    Page<Estoque> findAllOfEstoque(Pageable pageable);

    @Query("Select est FROM Estoque est " +
            "INNER JOIN FETCH est.material mat " +
            "LEFT JOIN FETCH est.material.categoria cat " +
            "LEFT JOIN FETCH est.material.fornecedor forn " +
            "LEFT JOIN FETCH est.setor setor " +
            "LEFT JOIN FETCH est.storageEndereco storage " +
            "LEFT JOIN FETCH est.material.unidade unit " +
            "WHERE est.id = :id")
    Optional<Estoque> findEstoqueById(@Param("id") Integer id);

    @Query("Select est FROM Estoque est " +
            "INNER JOIN FETCH est.material mat " +
            "LEFT JOIN FETCH est.material.categoria cat " +
            "LEFT JOIN FETCH est.material.fornecedor forn " +
            "LEFT JOIN FETCH est.setor setor " +
            "LEFT JOIN FETCH est.storageEndereco storage " +
            "LEFT JOIN FETCH est.material.unidade unit " +
            "WHERE CONCAT(est.id, ' ', mat.material, ' ', cat.categoria, ' ', setor.setor, ' ',forn.fornecedor) LIKE %?1%")
    Page<Estoque> findAllByKeyword(String keyword, Pageable pageable);

    @Query("Select est FROM Estoque est " +
            "INNER JOIN FETCH est.material mat " +
            "LEFT JOIN FETCH est.material.categoria cat " +
            "LEFT JOIN FETCH est.material.fornecedor forn " +
            "LEFT JOIN FETCH est.setor setor " +
            "LEFT JOIN FETCH est.storageEndereco storage " +
            "LEFT JOIN FETCH est.material.unidade unit " +
            "WHERE mat.id = :id")
    Page<Estoque> findAllByMaterial(@Param("id") Integer id, Pageable pageable);

    @Transactional
    @Query("Select est FROM Estoque est LEFT JOIN FETCH est.arquivos arq WHERE est.id = :eqId")
    Estoque findArquivosByEstoque(@Param("eqId") Integer eqId);

    List<Estoque> findAllByArquivos(Arquivos arquivos);

}
