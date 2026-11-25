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

public class RegisterActivity extends AppCompatActivity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout page = Ui.page(this); page.addView(Ui.text(this, "Registro de jugador"));
        var name = Ui.input(this, "Nombre"); var email = Ui.input(this, "Email"); var password = Ui.input(this, "Contraseña (mínimo 6 caracteres)");
        password.setInputType(129); page.addView(name); page.addView(email); page.addView(password);
        var submit = Ui.button(this, "Crear cuenta"); page.addView(submit);
        submit.setOnClickListener(v -> {
            String n = name.getText().toString().trim(), e = email.getText().toString().trim(), p = password.getText().toString();
            if (n.isEmpty() || e.isEmpty() || p.length() < 6) { Toast.makeText(this, "Completa los campos correctamente.", Toast.LENGTH_SHORT).show(); return; }
            ApiClient.api().register(new Registro(n,e,p)).enqueue(new Callback<>() {
                @Override public void onResponse(Call<Jugador> call, Response<Jugador> response) {
                    if (response.isSuccessful()) { Toast.makeText(RegisterActivity.this, "Cuenta creada.", Toast.LENGTH_SHORT).show(); startActivity(new Intent(RegisterActivity.this, LoginActivity.class)); finish(); }
                    else Toast.makeText(RegisterActivity.this, Ui.errorMessage(response), Toast.LENGTH_LONG).show();
                }
                @Override public void onFailure(Call<Jugador> call, Throwable error) { Toast.makeText(RegisterActivity.this, "No se pudo conectar con la API.", Toast.LENGTH_LONG).show(); }
            });
        });
        setContentView(page);
    }
}
