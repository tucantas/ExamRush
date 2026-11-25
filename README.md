# Exam Rush · EETAC

Versión inicial del proyecto, creada desde cero a partir de las tareas del tablero. Incluye una API REST en Java, páginas HTML independientes sin estilos y una app Android en Java.

## Qué incluye

- **Web:** páginas fuente separadas en `public/`, sencillas y sin CSS. Maven las sirve desde el backend. La tienda consulta la API y permite comprar y devolver objetos; el perfil muestra inventario e historial.
- **Android:** registro, login, catálogo, compra, devolución, partida y ranking.
- **Partida:** juego 2D sencillo. El jugador se mueve tocando la pantalla, recoge libros y evita distracciones (un móvil). Cada libro suma 10 puntos; la distracción resta 5. Al terminar se guarda puntuación y duración y aparece una nota orientativa.
- **API:** Spring Boot con rutas REST, validación, respuestas JSON de error y Swagger UI.
- **Base de datos:** H2 relacional en archivo local, con las tablas `jugador`, `partida`, `objeto` e `inventario`.
- **Pruebas:** recorrido HTTP integrado para registro, login, catálogo, compra, devolución, partida, ranking, errores y Swagger.

## Ejecutar el backend y la web

Requisitos: Java 17 y Maven 3.9 o compatible.

Desde `backend`:

```bash
mvn spring-boot:run
```

Después abre:

- Web: <http://localhost:8080/>
- Swagger: <http://localhost:8080/swagger-ui.html>
- Consola H2 de desarrollo: <http://localhost:8080/h2-console>

La base de datos se guarda en `backend/data/`. El usuario y la contraseña de H2 son `sa` y vacía; JDBC URL: `jdbc:h2:file:./data/examrush`.

Para ejecutar las pruebas desde `backend`:

```bash
mvn test
```

## Ejecutar Android

Abre `android` con Android Studio y deja que Gradle sincronice el proyecto. Se requiere Android SDK 35. Inicia primero el backend en el mismo ordenador y después la app en el emulador; `10.0.2.2` es la dirección del ordenador vista desde el emulador.

Para usar un teléfono físico, cambia `BASE_URL` en `android/app/src/main/java/es/examrush/android/ApiClient.java` por la dirección local del ordenador, por ejemplo `http://192.168.1.20:8080/api/v1/`. El dispositivo y el ordenador deben estar en la misma red.

## Decisiones provisionales

- Cada cuenta comienza con 100 puntos de saldo. La tienda trae tres objetos de ejemplo con precios y existencias configurables en `SeedData.java`.
- La devolución repone el saldo y las existencias al precio de compra actual.
- La partida dura 45 segundos. La nota se calcula inicialmente así: menos de 40 puntos = 0; 40–59 = 5; 60–79 = 7; 80–99 = 9; 100 o más = 10.
- Las páginas web se sirven desde el mismo backend para que puedan llamar a la API sin configuración adicional.

El grupo debe confirmar el saldo, el catálogo y la regla de nota antes de presentar esos valores como definitivos.

## Alcance de seguridad

Es una demo académica: las contraseñas se guardan cifradas, pero el login no crea tokens y las peticiones de tienda identifican al jugador mediante su ID. No está preparada para publicar en Internet como servicio real. El H2 console y HTTP sin cifrar están activados para desarrollo local.

## Integración y reparto

Los flujos de `.github/workflows` para ejecutar las pruebas y compilar Android están preparados en el paquete de pruebas/integración. La división sugerida para que cada integrante haga su propio commit está en `CONTRIBUTIONS.md`.
