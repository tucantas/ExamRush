package es.examrush.api.dto;

import es.examrush.model.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class ApiDtos {
    private ApiDtos() {}

    public record RegistroRequest(@NotBlank @Size(max = 60) String nombre,
                                  @NotBlank @Email @Size(max = 160) String email,
                                  @NotBlank @Size(min = 6, max = 72) String password) {}
    public record LoginRequest(@NotBlank @Email @Size(max = 160) String email, @NotBlank String password) {}
    public record MovimientoRequest(@NotNull @Positive Long jugadorId,
                                    @NotNull @Positive Long objetoId,
                                    @NotNull @Min(1) @Max(100) Integer cantidad) {}
    public record PartidaRequest(@NotNull @Positive Long jugadorId,
                                 @Min(0) @Max(1000000) int puntuacion,
                                 @NotNull @Min(1) @Max(300) Integer duracionSegundos) {}

    public record JugadorDto(Long id, String nombre, String email, BigDecimal saldo) {
        public static JugadorDto from(Jugador jugador) {
            return new JugadorDto(jugador.getId(), jugador.getNombre(), jugador.getEmail(), jugador.getSaldo());
        }
    }
    public record ObjetoDto(Long id, String nombre, String descripcion, BigDecimal precio, int existencias) {
        public static ObjetoDto from(Objeto objeto) {
            return new ObjetoDto(objeto.getId(), objeto.getNombre(), objeto.getDescripcion(), objeto.getPrecio(), objeto.getExistencias());
        }
    }
    public record InventarioDto(Long jugadorId, Long objetoId, String nombreObjeto, int cantidad) {
        public static InventarioDto from(Inventario inventario) {
            return new InventarioDto(inventario.getJugador().getId(), inventario.getObjeto().getId(), inventario.getObjeto().getNombre(), inventario.getCantidad());
        }
    }
    public record PartidaDto(Long id, Long jugadorId, int puntuacion, int duracionSegundos,
                             BigDecimal notaExamen, LocalDateTime fecha) {
        public static PartidaDto from(Partida partida) {
            return new PartidaDto(partida.getId(), partida.getJugador().getId(), partida.getPuntuacion(),
                    partida.getDuracionSegundos(), partida.getNotaExamen(), partida.getFecha());
        }
    }
    public record RankingDto(int puesto, Long jugadorId, String nombre, int puntuacion, LocalDateTime fecha) {}
    public record MensajeDto(String mensaje, JugadorDto jugador, InventarioDto inventario) {}
    public record ErrorDto(LocalDateTime fecha, int status, String error, String mensaje, String ruta) {}
}
