package es.examrush.android;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {
    // En un emulador Android, 10.0.2.2 apunta al ordenador que ejecuta el backend.
    // En un teléfono real, sustituir por la IP local del ordenador.
    private static final String BASE_URL = "http://10.0.2.2:8080/api/v1/";
    private static final ApiService API = new Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build().create(ApiService.class);
    private ApiClient() {}
    public static ApiService api() { return API; }
}
