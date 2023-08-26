package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Grupo;
import br.com.lablims.spring_lablims.domain.Usuario;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    @EntityGraph(attributePaths = "grupos")
    Usuario findByUsernameIgnoreCase(String username);

    Page<Usuario> findAllById(Integer id, Pageable pageable);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    List<Usuario> findAllByGrupos(Grupo grupo);

    Usuario findFirstByGrupos(Grupo grupo);

    Optional<Usuario> findByUsername(String username);

}
