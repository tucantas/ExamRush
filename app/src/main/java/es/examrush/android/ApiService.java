package es.examrush.android;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static es.examrush.android.ApiModels.*;

public interface ApiService {
    @POST("usuarios/registro") Call<Jugador> register(@Body Registro request);
    @POST("usuarios/login") Call<Jugador> login(@Body Login request);
    @GET("tienda/objetos") Call<List<Objeto>> objects();
    @POST("tienda/compras") Call<Mensaje> buy(@Body Movimiento request);
    @POST("tienda/devoluciones") Call<Mensaje> returnItem(@Body Movimiento request);
    @GET("jugadores/{id}/inventario") Call<List<Inventario>> inventory(@Path("id") long id);
    @GET("jugadores/{id}") Call<Jugador> player(@Path("id") long id);
    @GET("jugadores/{id}/partidas") Call<List<Partida>> history(@Path("id") long id);
    @POST("partidas") Call<Partida> saveMatch(@Body PartidaRequest request);
    @GET("ranking") Call<List<Ranking>> ranking(@Query("limite") int limit);
}
