package es.examrush.repository;

import es.examrush.model.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JugadorRepository extends JpaRepository<Jugador, Long> {
    Optional<Jugador> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
