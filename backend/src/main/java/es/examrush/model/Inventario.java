package es.examrush.model;

import jakarta.persistence.*;

@Entity
@Table(name = "inventario", uniqueConstraints = @UniqueConstraint(name = "uk_inventario_jugador_objeto", columnNames = {"jugador_id", "objeto_id"}))
public class Inventario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "jugador_id", nullable = false)
    private Jugador jugador;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "objeto_id", nullable = false)
    private Objeto objeto;
    @Column(nullable = false)
    private int cantidad;

    protected Inventario() {}
    public Inventario(Jugador jugador, Objeto objeto, int cantidad) { this.jugador = jugador; this.objeto = objeto; this.cantidad = cantidad; }
    public Long getId() { return id; }
    public Jugador getJugador() { return jugador; }
    public Objeto getObjeto() { return objeto; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
