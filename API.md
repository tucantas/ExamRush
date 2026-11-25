# Contrato de la API · Exam Rush

Propuesta implementada para que Android y la web utilicen las mismas respuestas. El saldo, los objetos, los precios, los puntos y la escala de nota son valores iniciales de demostración y deben confirmarse con el grupo.

**Base:** `/api/v1` · **Formato:** JSON

## Representaciones

Jugador (la contraseña nunca se devuelve):

```json
{"id":1,"nombre":"Alex","email":"alex@example.com","saldo":100.00}
```

Objeto:

```json
{"id":1,"nombre":"Apuntes de Java","descripcion":"Resumen para estudiar","precio":20.00,"existencias":25}
```

Partida:

```json
{"id":1,"jugadorId":1,"puntuacion":85,"duracionSegundos":45,"notaExamen":9.0,"fecha":"2026-11-25T10:30:00"}
```

Las respuestas son DTOs planos para que Jugador y Partida no formen referencias circulares. El inventario representa la relación entre jugador y objeto.

## Servicios

| Método y ruta | Uso | Éxito |
|---|---|---|
| `POST /usuarios/registro` | Crear cuenta. Entrada: `{"nombre":"Alex","email":"alex@example.com","password":"exam1234"}` | `201`, Jugador |
| `POST /usuarios/login` | Comprobar email y contraseña. Entrada: `{"email":"alex@example.com","password":"exam1234"}` | `200`, Jugador |
| `GET /tienda/objetos` | Listar catálogo | `200`, lista de Objeto |
| `POST /tienda/compras` | Comprar. Entrada: `{"jugadorId":1,"objetoId":1,"cantidad":1}` | `200`, mensaje, saldo actualizado e inventario |
| `POST /tienda/devoluciones` | Devolver objeto. Misma entrada que compra | `200`, mensaje y saldo actualizado |
| `GET /jugadores/{id}` | Consultar perfil y saldo | `200`, Jugador |
| `GET /jugadores/{id}/inventario` | Consultar inventario | `200`, lista de objetos y cantidades |
| `POST /partidas` | Guardar puntuación y duración. Entrada: `{"jugadorId":1,"puntuacion":85,"duracionSegundos":45}` | `201`, partida con nota calculada |
| `GET /jugadores/{id}/partidas` | Consultar historial personal | `200`, lista de partidas recientes |
| `GET /ranking?limite=10` | Consultar mejores resultados, máximo 10 | `200`, lista ordenada por puntuación |

Ejemplo de respuesta de compra:

```json
{
  "mensaje":"Compra realizada.",
  "jugador":{"id":1,"nombre":"Alex","email":"alex@example.com","saldo":80.00},
  "inventario":{"jugadorId":1,"objetoId":1,"nombreObjeto":"Apuntes de Java","cantidad":1}
}
```

La devolución repone las existencias y el saldo al precio actual del objeto. Solo se puede devolver una cantidad igual o inferior a la que consta en el inventario.

## Errores HTTP

Las respuestas de error usan el mismo formato:

```json
{"fecha":"2026-11-25T10:30:00","status":409,"error":"Conflict","mensaje":"No hay existencias suficientes.","ruta":"/api/v1/tienda/compras"}
```

- `400 Bad Request`: JSON o campos inválidos.
- `404 Not Found`: jugador u objeto inexistente (o email de login no registrado).
- `409 Conflict`: email duplicado, saldo/stock insuficiente o devolución no permitida.

## Base de datos

- `jugador`: nombre, email único, hash de contraseña y saldo.
- `partida`: jugador, puntuación, duración en segundos, nota calculada y fecha.
- `objeto`: nombre, descripción, precio y existencias.
- `inventario`: jugador, objeto y cantidad; combinación jugador/objeto única.

## Reglas iniciales del juego (provisionales)

La partida dura 45 segundos. Cada libro suma 10 puntos y cada distracción resta 5. La nota orientativa se calcula así:

| Puntuación | Nota |
|---:|---:|
| 0–39 | 0 |
| 40–59 | 5 |
| 60–79 | 7 |
| 80–99 | 9 |
| 100 o más | 10 |

Swagger UI está disponible en `/swagger-ui.html`; el documento OpenAPI JSON está en `/v3/api-docs`.
