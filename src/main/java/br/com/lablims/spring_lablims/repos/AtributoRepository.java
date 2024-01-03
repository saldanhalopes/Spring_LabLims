package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Atributo;
import br.com.lablims.spring_lablims.domain.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface AtributoRepository extends JpaRepository<Atributo, Integer> {

    Page<Atributo> findAllById(Integer id, Pageable pageable);

    @Query("Select atr FROM Atributo atr LEFT JOIN FETCH atr.categoria cat")
    Page<Atributo> findAllOfAtributos(Pageable pageable);

}
