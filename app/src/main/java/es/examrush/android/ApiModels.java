package es.examrush.android;

import java.util.List;

public final class ApiModels {
    private ApiModels() {}
    public static class Registro { public String nombre, email, password; public Registro(String n,String e,String p){nombre=n;email=e;password=p;} }
    public static class Login { public String email, password; public Login(String e,String p){email=e;password=p;} }
    public static class Movimiento { public long jugadorId, objetoId; public int cantidad; public Movimiento(long j,long o,int c){jugadorId=j;objetoId=o;cantidad=c;} }
    public static class PartidaRequest { public long jugadorId; public int puntuacion; public PartidaRequest(long j,int p){jugadorId=j;puntuacion=p;} }
    public static class Jugador { public long id; public String nombre,email; public double saldo; }
    public static class Objeto { public long id; public String nombre,descripcion; public double precio; public int existencias; public String toString(){return id+" · "+nombre+" · "+precio+" puntos · stock "+existencias;} }
    public static class Inventario { public long jugadorId,objetoId; public String nombreObjeto; public int cantidad; public String toString(){return nombreObjeto+" · "+cantidad;} }
    public static class Mensaje { public String mensaje; public Jugador jugador; public Inventario inventario; }
    public static class Partida { public long id,jugadorId; public int puntuacion; public double notaExamen; public String fecha; }
    public static class Ranking { public int puesto; public String nombre; public int puntuacion; }
    public static class Error { public String mensaje; }
}
