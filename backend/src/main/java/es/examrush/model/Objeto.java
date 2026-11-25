package es.examrush.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "objeto")
public class Objeto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 80)
    private String nombre;
    @Column(length = 240)
    private String descripcion;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    @Column(nullable = false)
    private int existencias;

    protected Objeto() {}
    public Objeto(String nombre, String descripcion, BigDecimal precio, int existencias) {
        this.nombre = nombre; this.descripcion = descripcion; this.precio = precio; this.existencias = existencias;
    }
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public BigDecimal getPrecio() { return precio; }
    public int getExistencias() { return existencias; }
    public void setExistencias(int existencias) { this.existencias = existencias; }
}
