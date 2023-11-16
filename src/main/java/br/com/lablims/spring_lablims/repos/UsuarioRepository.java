package br.com.lablims.spring_lablims.repos;

import br.com.lablims.spring_lablims.domain.Grupo;
import br.com.lablims.spring_lablims.domain.Lote;
import br.com.lablims.spring_lablims.domain.Usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {


    Usuario findByUsernameIgnoreCase(String username);
    @Query("Select user FROM Usuario user INNER JOIN FETCH user.grupo gr WHERE user.username = :username ")
    Usuario findByUsernameWithGrupo(@Param("username") String username);

    Page<Usuario> findAllById(Integer id, Pageable pageable);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    Usuario findFirstByGrupo(Grupo grupo);

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findBySecret(String Secret);

}
