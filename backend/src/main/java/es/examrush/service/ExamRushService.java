package es.examrush.service;

import es.examrush.api.ApiException;
import es.examrush.api.dto.ApiDtos.*;
import es.examrush.model.*;
import es.examrush.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class ExamRushService {
    private static final BigDecimal SALDO_INICIAL = new BigDecimal("100.00");
    private final JugadorRepository jugadores;
    private final ObjetoRepository objetos;
    private final InventarioRepository inventario;
    private final PartidaRepository partidas;
    private final PasswordEncoder encoder;

    public ExamRushService(JugadorRepository jugadores, ObjetoRepository objetos,
                           InventarioRepository inventario, PartidaRepository partidas,
                           PasswordEncoder encoder) {
        this.jugadores = jugadores; this.objetos = objetos; this.inventario = inventario;
        this.partidas = partidas; this.encoder = encoder;
    }

    @Transactional
    public JugadorDto registrar(RegistroRequest request) {
        if (jugadores.existsByEmailIgnoreCase(request.email()))
            throw new ApiException(HttpStatus.CONFLICT, "Ese email ya está registrado.");
        Jugador jugador = new Jugador(request.nombre().trim(), request.email().trim().toLowerCase(),
                encoder.encode(request.password()), SALDO_INICIAL);
        return JugadorDto.from(jugadores.save(jugador));
    }

    @Transactional(readOnly = true)
    public JugadorDto login(LoginRequest request) {
        Jugador jugador = jugadores.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No existe un jugador con ese email."));
        if (!encoder.matches(request.password(), jugador.getPasswordHash()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "La contraseña no es correcta.");
        return JugadorDto.from(jugador);
    }

    @Transactional(readOnly = true)
    public List<ObjetoDto> listarObjetos() {
        return objetos.findAll().stream().map(ObjetoDto::from).toList();
    }

    @Transactional
    public MensajeDto comprar(MovimientoRequest request) {
        Jugador jugador = jugador(request.jugadorId());
        Objeto objeto = objeto(request.objetoId());
        int cantidad = request.cantidad();
        if (objeto.getExistencias() < cantidad)
            throw new ApiException(HttpStatus.CONFLICT, "No hay existencias suficientes.");
        BigDecimal coste = objeto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
        if (jugador.getSaldo().compareTo(coste) < 0)
            throw new ApiException(HttpStatus.CONFLICT, "El saldo no es suficiente.");
        objeto.setExistencias(objeto.getExistencias() - cantidad);
        jugador.setSaldo(jugador.getSaldo().subtract(coste));
        Inventario fila = inventario.findByJugador_IdAndObjeto_Id(jugador.getId(), objeto.getId())
                .orElseGet(() -> new Inventario(jugador, objeto, 0));
        fila.setCantidad(fila.getCantidad() + cantidad);
        objetos.save(objeto);
        jugadores.save(jugador);
        InventarioDto item = InventarioDto.from(inventario.save(fila));
        return new MensajeDto("Compra realizada.", JugadorDto.from(jugador), item);
    }

    @Transactional
    public MensajeDto devolver(MovimientoRequest request) {
        Jugador jugador = jugador(request.jugadorId());
        Objeto objeto = objeto(request.objetoId());
        Inventario fila = inventario.findByJugador_IdAndObjeto_Id(jugador.getId(), objeto.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.CONFLICT, "El objeto no está en el inventario del jugador."));
        int cantidad = request.cantidad();
        if (fila.getCantidad() < cantidad)
            throw new ApiException(HttpStatus.CONFLICT, "No se puede devolver más unidades de las que se tienen.");
        fila.setCantidad(fila.getCantidad() - cantidad);
        objeto.setExistencias(objeto.getExistencias() + cantidad);
        jugador.setSaldo(jugador.getSaldo().add(objeto.getPrecio().multiply(BigDecimal.valueOf(cantidad))));
        objetos.save(objeto);
        jugadores.save(jugador);
        InventarioDto item = fila.getCantidad() == 0 ? null : InventarioDto.from(inventario.save(fila));
        if (fila.getCantidad() == 0) inventario.delete(fila);
        return new MensajeDto("Devolución realizada.", JugadorDto.from(jugador), item);
    }

    @Transactional(readOnly = true)
    public List<InventarioDto> listarInventario(Long jugadorId) {
        jugador(jugadorId);
        return inventario.findByJugador_IdOrderByObjeto_Nombre(jugadorId).stream().map(InventarioDto::from).toList();
    }

    @Transactional(readOnly = true)
    public JugadorDto consultarJugador(Long jugadorId) {
        return JugadorDto.from(jugador(jugadorId));
    }

    @Transactional(readOnly = true)
    public List<PartidaDto> historial(Long jugadorId) {
        jugador(jugadorId);
        return partidas.findByJugador_IdOrderByFechaDesc(jugadorId).stream().map(PartidaDto::from).toList();
    }

    @Transactional
    public PartidaDto guardarPartida(PartidaRequest request) {
        Partida partida = new Partida(jugador(request.jugadorId()), request.puntuacion(), request.duracionSegundos(),
                nota(request.puntuacion()), LocalDateTime.now());
        return PartidaDto.from(partidas.save(partida));
    }

    @Transactional(readOnly = true)
    public List<RankingDto> ranking(int limite) {
        if (limite < 1 || limite > 10) throw new ApiException(HttpStatus.BAD_REQUEST, "El límite debe estar entre 1 y 10.");
        List<Partida> top = partidas.findTop10ByOrderByPuntuacionDescFechaAsc().stream().limit(limite).toList();
        return IntStream.range(0, top.size()).mapToObj(i -> {
            Partida p = top.get(i);
            return new RankingDto(i + 1, p.getJugador().getId(), p.getJugador().getNombre(), p.getPuntuacion(), p.getFecha());
        }).toList();
    }

    private Jugador jugador(Long id) {
        return jugadores.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No existe el jugador."));
    }
    private Objeto objeto(Long id) {
        return objetos.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No existe el objeto."));
    }

    private BigDecimal nota(int puntuacion) {
        // Escala de demostración provisional, pendiente de validar con el grupo.
        if (puntuacion >= 100) return new BigDecimal("10.0");
        if (puntuacion >= 80) return new BigDecimal("9.0");
        if (puntuacion >= 60) return new BigDecimal("7.0");
        if (puntuacion >= 40) return new BigDecimal("5.0");
        return new BigDecimal("0.0");
    }
}
