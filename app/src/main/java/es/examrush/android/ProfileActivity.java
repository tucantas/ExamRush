package es.examrush.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import static es.examrush.android.ApiModels.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private LinearLayout rows;
    private LinearLayout historyRows;
    private LinearLayout inventoryRows;
    private long playerId;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        playerId = Ui.playerId(this);
        if (playerId < 1) {
            Toast.makeText(this, "Inicia sesión para consultar tu perfil.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class)); finish(); return;
        }
        LinearLayout page = Ui.page(this); page.addView(Ui.text(this, "Mi perfil e historial"));
        var back = Ui.button(this, "Volver"); page.addView(back); back.setOnClickListener(v -> finish());
        ScrollView scroll = new ScrollView(this); rows = new LinearLayout(this); rows.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(rows, new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        page.addView(scroll); setContentView(page); load();
    }

    private void load() {
        rows.addView(Ui.text(this, "Cargando perfil…"));
        ApiClient.api().player(playerId).enqueue(new Callback<>() {
            @Override public void onResponse(Call<Jugador> call, Response<Jugador> response) {
                rows.removeAllViews();
                if (!response.isSuccessful() || response.body() == null) { showError(response); return; }
                Jugador player = response.body();
                rows.addView(Ui.text(ProfileActivity.this, "Nombre: " + player.nombre));
                rows.addView(Ui.text(ProfileActivity.this, "Email: " + player.email));
                rows.addView(Ui.text(ProfileActivity.this, "Saldo: " + player.saldo + " puntos"));
                rows.addView(Ui.text(ProfileActivity.this, "Historial de partidas"));
                historyRows = new LinearLayout(ProfileActivity.this); historyRows.setOrientation(LinearLayout.VERTICAL); rows.addView(historyRows);
                rows.addView(Ui.text(ProfileActivity.this, "Inventario"));
                inventoryRows = new LinearLayout(ProfileActivity.this); inventoryRows.setOrientation(LinearLayout.VERTICAL); rows.addView(inventoryRows);
                loadHistory();
                loadInventory();
            }
            @Override public void onFailure(Call<Jugador> call, Throwable error) { showConnectionError(); }
        });
    }

    private void loadHistory() {
        ApiClient.api().history(playerId).enqueue(new Callback<>() {
            @Override public void onResponse(Call<List<Partida>> call, Response<List<Partida>> response) {
                if (!response.isSuccessful() || response.body() == null) { showError(response); return; }
                if (response.body().isEmpty()) historyRows.addView(Ui.text(ProfileActivity.this, "Todavía no hay partidas."));
                for (Partida match : response.body()) historyRows.addView(Ui.text(ProfileActivity.this,
                        match.puntuacion + " puntos · nota " + match.notaExamen + "/10 · " + match.fecha));
            }
            @Override public void onFailure(Call<List<Partida>> call, Throwable error) { showConnectionError(); }
        });
    }

    private void loadInventory() {
        ApiClient.api().inventory(playerId).enqueue(new Callback<>() {
            @Override public void onResponse(Call<List<Inventario>> call, Response<List<Inventario>> response) {
                if (!response.isSuccessful() || response.body() == null) { showError(response); return; }
                if (response.body().isEmpty()) inventoryRows.addView(Ui.text(ProfileActivity.this, "Sin objetos."));
                for (Inventario item : response.body()) inventoryRows.addView(Ui.text(ProfileActivity.this, item.toString()));
            }
            @Override public void onFailure(Call<List<Inventario>> call, Throwable error) { showConnectionError(); }
        });
    }

    private void showError(Response<?> response) { Toast.makeText(this, Ui.errorMessage(response), Toast.LENGTH_LONG).show(); }
    private void showConnectionError() { Toast.makeText(this, "No se pudo conectar con la API.", Toast.LENGTH_LONG).show(); }
}
