# Reparto sugerido para commits del equipo

Cada integrante debe subir su parte con su propia cuenta de GitHub. El commit queda asociado al usuario que lo publica; no se deben escribir nombres de compañeros como autores si no han realizado ese commit.

| Parte | Tareas | Archivos principales |
|---|---|---|
| Web | #1–#6 | `webapp/` |
| Android | #7–#11 | `android/` |
| Backend y base de datos | #12–#17 | `backend/src/main/`, `backend/pom.xml`, `API.md` |
| Pruebas e integración | #18–#19 | `backend/src/test/`, `.github/workflows/`, `README.md` |

## Flujo sencillo

1. Una persona clona el repositorio y copia dentro la carpeta que tiene asignada.
2. Revisa que sus archivos estén en el área correspondiente y crea un commit con su cuenta.
3. Sube la rama y abre una pull request, o hace push a la rama acordada por el equipo.
4. El grupo integra Web, backend y Android y espera a que las dos acciones automáticas terminen correctamente.

Mensajes de commit recomendados: `Web: conectar tienda a la API`, `Android: añadir registro y login`, `Backend: crear servicios de tienda`, `Testing: probar el flujo completo`.

La web se mantiene en `webapp/`; Maven la copia al paquete estático del backend durante la compilación. Así la carpeta de trabajo de Web queda separada de la implementación de la API.

**Estado de la publicación:** el repositorio tenía solo un README inicial; ese único commit lo creó la cuenta `tucantas`. El código de las carpetas de este paquete aún debe subirse por los integrantes responsables de cada parte.
