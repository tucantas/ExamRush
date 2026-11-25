package es.examrush.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "partida")
public class Partida {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_id", nullable = false)
    private Jugador jugador;
    @Column(nullable = false)
    private int puntuacion;
    @Column(name = "duracion_segundos", nullable = false)
    private int duracionSegundos;
    @Column(name = "nota_examen", nullable = false, precision = 3, scale = 1)
    private BigDecimal notaExamen;
    @Column(nullable = false)
    private LocalDateTime fecha;

    protected Partida() {}
    public Partida(Jugador jugador, int puntuacion, int duracionSegundos, BigDecimal notaExamen, LocalDateTime fecha) {
        this.jugador = jugador; this.puntuacion = puntuacion; this.duracionSegundos = duracionSegundos;
        this.notaExamen = notaExamen; this.fecha = fecha;
    }
    public Long getId() { return id; }
    public Jugador getJugador() { return jugador; }
    public int getPuntuacion() { return puntuacion; }
    public int getDuracionSegundos() { return duracionSegundos; }
    public BigDecimal getNotaExamen() { return notaExamen; }
    public LocalDateTime getFecha() { return fecha; }
}
