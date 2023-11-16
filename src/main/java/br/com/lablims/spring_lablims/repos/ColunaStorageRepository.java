package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Storage;
import br.com.lablims.spring_lablims.domain.StorageTipo;
import br.com.lablims.spring_lablims.domain.Setor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ColunaStorageRepository extends JpaRepository<Storage, Integer> {

    Page<Storage> findAllById(Integer id, Pageable pageable);

    Storage findFirstBySetor(Setor setor);

    Storage findFirstByTipo(StorageTipo storageTipo);

}
