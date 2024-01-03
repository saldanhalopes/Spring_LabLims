package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.EstoqueSaldo;
import br.com.lablims.spring_lablims.domain.Produto;
import br.com.lablims.spring_lablims.domain.ProdutoTipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

    Page<Produto> findAllById(Integer id, Pageable pageable);

    @Query("Select prod FROM Produto prod " +
            "INNER JOIN FETCH prod.produtoTipo tipo ")
    Page<Produto> findAllOfProduto(Pageable pageable);

    @Query("Select prod FROM Produto prod " +
            "INNER JOIN FETCH prod.produtoTipo tipo " +
            "WHERE CONCAT(prod.id, ' ', prod.codigo, ' ', prod.controleEspecial, ' ', tipo.tipo, ' ',tipo.sigla) LIKE %?1%")
    Page<Produto> findAllByKeyword(String keyword, Pageable pageable);

    @Query("Select prod FROM Produto prod " +
            "INNER JOIN FETCH prod.produtoTipo tipo " +
            "WHERE prod.id = :id")
    Optional<Produto> findProdutoById(@Param("id") Integer id);

    Produto findFirstByProdutoTipo(ProdutoTipo produtoTipo);



}
