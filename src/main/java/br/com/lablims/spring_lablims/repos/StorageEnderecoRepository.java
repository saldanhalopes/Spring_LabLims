package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Storage;
import br.com.lablims.spring_lablims.domain.StorageEndereco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StorageEnderecoRepository extends JpaRepository<StorageEndereco, Integer> {

    Page<StorageEndereco> findAllById(Integer id, Pageable pageable);

    StorageEndereco findFirstByStorage(Storage storage);

}
