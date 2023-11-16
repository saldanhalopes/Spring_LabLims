package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Seguranca;
import br.com.lablims.spring_lablims.enums.SegurancaTipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Map;


public interface SegurancaRepository extends JpaRepository<Seguranca, Integer> {

    Seguranca findBySegurancaTipo(SegurancaTipo value);
}
