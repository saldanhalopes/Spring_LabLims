package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Atributo;
import br.com.lablims.spring_lablims.domain.Coluna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ColunaRepository extends JpaRepository<Coluna, Integer> {

    Page<Coluna> findAllById(Integer id, Pageable pageable);

    Coluna findFirstByTipoColuna(Atributo atributo);

    Coluna findFirstByFabricanteColuna(Atributo atributo);

    Coluna findFirstByMarcaColuna(Atributo atributo);

    Coluna findFirstByFaseColuna(Atributo atributo);

    Coluna findFirstByTamanhoColuna(Atributo atributo);

    Coluna findFirstByDiametroColuna(Atributo atributo);

    Coluna findFirstByParticulaColuna(Atributo atributo);

}
