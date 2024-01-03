package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface StorageRepository extends JpaRepository<Storage, Integer> {

    Page<Storage> findAllById(Integer id, Pageable pageable);

    @Query("Select store FROM Storage store " +
            "LEFT JOIN FETCH store.setor setor " +
            "LEFT JOIN FETCH store.tipo tipo " +
            "WHERE store.id = :id")
    Optional<Storage> findStorageById(@Param("id") Integer id);

    @Query("Select store FROM Storage store " +
            "LEFT JOIN FETCH store.setor setor " +
            "LEFT JOIN FETCH store.tipo tipo")
    Page<Storage> findAllOfStorages(Pageable pageable);


    Storage findFirstBySetor(Setor setor);

    Storage findFirstByTipo(StorageTipo storageTipo);

}
