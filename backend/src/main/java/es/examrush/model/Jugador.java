package es.examrush.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "jugador", uniqueConstraints = @UniqueConstraint(name = "uk_jugador_email", columnNames = "email"))
public class Jugador {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 60)
    private String nombre;
    @Column(nullable = false, length = 160)
    private String email;
    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    protected Jugador() {}
    public Jugador(String nombre, String email, String passwordHash, BigDecimal saldo) {
        this.nombre = nombre; this.email = email; this.passwordHash = passwordHash; this.saldo = saldo;
    }
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
}
