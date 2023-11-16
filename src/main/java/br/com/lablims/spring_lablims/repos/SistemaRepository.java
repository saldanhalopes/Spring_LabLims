package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Sistema;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SistemaRepository extends JpaRepository<Sistema, Integer> {

    Sistema findBySistemaNome(String nomeSistema);
}
