package es.examrush;

import es.examrush.repository.InventarioRepository;
import es.examrush.repository.JugadorRepository;
import es.examrush.repository.ObjetoRepository;
import es.examrush.repository.PartidaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class ExamRushFlowTest {
    @Autowired MockMvc mvc;
    @Autowired PartidaRepository partidas;
    @Autowired InventarioRepository inventarios;
    @Autowired JugadorRepository jugadores;
    @Autowired ObjetoRepository objetos;

    @BeforeEach
    void limpiar() {
        partidas.deleteAll(); inventarios.deleteAll(); jugadores.deleteAll(); objetos.deleteAll();
        objetos.save(new es.examrush.model.Objeto("Libro de prueba", "Objeto de test", new java.math.BigDecimal("20.00"), 3));
    }

    @Test
    void flujoCompletoRegistroCompraDevolucionPartidaRanking() throws Exception {
        MvcResult registro = mvc.perform(post("/api/v1/usuarios/registro").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Alex","email":"alex@example.com","password":"exam1234"}
                                """))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.saldo").value(100.0)).andReturn();
        long jugadorId = ((Number) JsonPath.read(registro.getResponse().getContentAsString(), "$.id")).longValue();

        mvc.perform(post("/api/v1/usuarios/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"alex@example.com","password":"exam1234"}
                                """))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value((int) jugadorId));

        MvcResult catalogo = mvc.perform(get("/api/v1/tienda/objetos"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1))).andReturn();
        long objetoId = ((Number) JsonPath.read(catalogo.getResponse().getContentAsString(), "$[0].id")).longValue();
        String movimiento = "{\"jugadorId\":" + jugadorId + ",\"objetoId\":" + objetoId + ",\"cantidad\":1}";

        mvc.perform(post("/api/v1/tienda/compras").contentType(MediaType.APPLICATION_JSON).content(movimiento))
                .andExpect(status().isOk()).andExpect(jsonPath("$.jugador.saldo").value(80.0))
                .andExpect(jsonPath("$.inventario.cantidad").value(1));
        mvc.perform(get("/api/v1/jugadores/{id}/inventario", jugadorId))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(post("/api/v1/tienda/devoluciones").contentType(MediaType.APPLICATION_JSON).content(movimiento))
                .andExpect(status().isOk()).andExpect(jsonPath("$.jugador.saldo").value(100.0));
        mvc.perform(get("/api/v1/jugadores/{id}/inventario", jugadorId))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));

        mvc.perform(post("/api/v1/partidas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"jugadorId\":" + jugadorId + ",\"puntuacion\":850,\"duracionSegundos\":45}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.puntuacion").value(850))
                .andExpect(jsonPath("$.notaExamen").value(10.0));
        mvc.perform(get("/api/v1/ranking?limite=10"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].puesto").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Alex"));
        mvc.perform(get("/api/v1/jugadores/{id}", jugadorId))
                .andExpect(status().isOk()).andExpect(jsonPath("$.email").value("alex@example.com"));
        mvc.perform(get("/api/v1/jugadores/{id}/partidas", jugadorId))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].notaExamen").value(10.0));
    }

    @Test
    void erroresHttpSon400404Y409() throws Exception {
        mvc.perform(post("/api/v1/usuarios/registro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"email\":\"no-es-email\",\"password\":\"123\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));

        mvc.perform(get("/api/v1/jugadores/999/inventario"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));

        String registro = "{\"nombre\":\"Sam\",\"email\":\"sam@example.com\",\"password\":\"exam1234\"}";
        mvc.perform(post("/api/v1/usuarios/registro").contentType(MediaType.APPLICATION_JSON).content(registro))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/v1/usuarios/registro").contentType(MediaType.APPLICATION_JSON).content(registro))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void swaggerEstaPublicado() throws Exception {
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk()).andExpect(jsonPath("$.info.title").value("Exam Rush API"));
    }

    @Test
    void paginasWebSeSirvenDesdeElProyecto() throws Exception {
        mvc.perform(get("/index.html")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("Exam Rush")));
        mvc.perform(get("/register.html")).andExpect(status().isOk());
        mvc.perform(get("/login.html")).andExpect(status().isOk());
        mvc.perform(get("/tienda.html")).andExpect(status().isOk());
        mvc.perform(get("/perfil.html")).andExpect(status().isOk());
        mvc.perform(get("/ranking.html")).andExpect(status().isOk());
    }
}
