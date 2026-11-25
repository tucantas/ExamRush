package es.examrush.repository;

import es.examrush.model.Partida;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PartidaRepository extends JpaRepository<Partida, Long> {
    List<Partida> findTop10ByOrderByPuntuacionDescFechaAsc();
    List<Partida> findByJugador_IdOrderByFechaDesc(Long jugadorId);
}
