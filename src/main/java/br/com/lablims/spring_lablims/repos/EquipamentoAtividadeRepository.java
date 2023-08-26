package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.EquipamentoAtividade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EquipamentoAtividadeRepository extends JpaRepository<EquipamentoAtividade, Integer> {

    Page<EquipamentoAtividade> findAllById(Integer id, Pageable pageable);

}
