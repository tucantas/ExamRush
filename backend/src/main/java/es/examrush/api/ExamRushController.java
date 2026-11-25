package es.examrush.api;

import es.examrush.api.dto.ApiDtos.*;
import es.examrush.service.ExamRushService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Exam Rush")
public class ExamRushController {
    private final ExamRushService service;
    public ExamRushController(ExamRushService service) { this.service = service; }

    @PostMapping("/usuarios/registro")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar jugador")
    public JugadorDto registro(@Valid @RequestBody RegistroRequest request) { return service.registrar(request); }

    @PostMapping("/usuarios/login")
    @Operation(summary = "Iniciar sesión")
    public JugadorDto login(@Valid @RequestBody LoginRequest request) { return service.login(request); }

    @GetMapping("/tienda/objetos")
    @Operation(summary = "Listar objetos disponibles")
    public List<ObjetoDto> objetos() { return service.listarObjetos(); }

    @PostMapping("/tienda/compras")
    @Operation(summary = "Comprar objetos y actualizar saldo e inventario")
    public MensajeDto comprar(@Valid @RequestBody MovimientoRequest request) { return service.comprar(request); }

    @PostMapping("/tienda/devoluciones")
    @Operation(summary = "Devolver objetos comprados")
    public MensajeDto devolver(@Valid @RequestBody MovimientoRequest request) { return service.devolver(request); }

    @GetMapping("/jugadores/{id}/inventario")
    @Operation(summary = "Consultar inventario de un jugador")
    public List<InventarioDto> inventario(@PathVariable Long id) { return service.listarInventario(id); }

    @GetMapping("/jugadores/{id}")
    @Operation(summary = "Consultar perfil de jugador")
    public JugadorDto jugador(@PathVariable Long id) { return service.consultarJugador(id); }

    @GetMapping("/jugadores/{id}/partidas")
    @Operation(summary = "Consultar historial de partidas de un jugador")
    public List<PartidaDto> historial(@PathVariable Long id) { return service.historial(id); }

    @PostMapping("/partidas")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar resultado de una partida")
    public PartidaDto partida(@Valid @RequestBody PartidaRequest request) { return service.guardarPartida(request); }

    @GetMapping("/ranking")
    @Operation(summary = "Consultar mejores puntuaciones")
    public List<RankingDto> ranking(@RequestParam(defaultValue = "10") int limite) { return service.ranking(limite); }
}
