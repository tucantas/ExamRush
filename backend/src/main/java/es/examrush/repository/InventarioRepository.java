package es.examrush.repository;

import es.examrush.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Optional<Inventario> findByJugador_IdAndObjeto_Id(Long jugadorId, Long objetoId);
    List<Inventario> findByJugador_IdOrderByObjeto_Nombre(Long jugadorId);
}
