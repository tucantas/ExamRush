package es.examrush.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import static es.examrush.android.ApiModels.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout page = Ui.page(this); page.addView(Ui.text(this, "Login"));
        var email = Ui.input(this, "Email"); var password = Ui.input(this, "Contraseña"); password.setInputType(129);
        page.addView(email); page.addView(password); var submit = Ui.button(this, "Entrar"); page.addView(submit);
        submit.setOnClickListener(v -> ApiClient.api().login(new Login(email.getText().toString().trim(), password.getText().toString())).enqueue(new Callback<>() {
            @Override public void onResponse(Call<Jugador> call, Response<Jugador> response) {
                if (!response.isSuccessful() || response.body() == null) { Toast.makeText(LoginActivity.this, Ui.errorMessage(response), Toast.LENGTH_LONG).show(); return; }
                Jugador player = response.body();
                getSharedPreferences("examrush", MODE_PRIVATE).edit().putLong("jugadorId", player.id).putString("nombre", player.nombre).apply();
                startActivity(new Intent(LoginActivity.this, StoreActivity.class)); finish();
            }
            @Override public void onFailure(Call<Jugador> call, Throwable error) { Toast.makeText(LoginActivity.this, "No se pudo conectar con la API.", Toast.LENGTH_LONG).show(); }
        }));
        setContentView(page);
    }
}
