package es.examrush.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import static es.examrush.android.ApiModels.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreActivity extends AppCompatActivity {
    private LinearLayout content;
    private long playerId;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        playerId = Ui.playerId(this);
        if (playerId < 1) {
            Toast.makeText(this, "Inicia sesión para usar la tienda.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class)); finish(); return;
        }
        LinearLayout page = Ui.page(this);
        page.addView(Ui.text(this, "Tienda de Exam Rush"));
        var back = Ui.button(this, "Volver al inicio"); page.addView(back); back.setOnClickListener(v -> finish());
        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(content, new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        page.addView(scroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        setContentView(page); load();
    }

    private void load() {
        content.removeAllViews(); content.addView(Ui.text(this, "Cargando tienda…"));
        ApiClient.api().objects().enqueue(new Callback<>() {
            @Override public void onResponse(Call<List<Objeto>> call, Response<List<Objeto>> response) {
                if (!response.isSuccessful() || response.body() == null) { showError(response); return; }
                content.removeAllViews();
                content.addView(Ui.text(StoreActivity.this, "Objetos disponibles"));
                for (Objeto item : response.body()) addItem(item);
                content.addView(Ui.text(StoreActivity.this, "Mi inventario"));
                ApiClient.api().inventory(playerId).enqueue(new Callback<>() {
                    @Override public void onResponse(Call<List<Inventario>> c, Response<List<Inventario>> r) {
                        if (!r.isSuccessful() || r.body() == null) { showError(r); return; }
                        if (r.body().isEmpty()) content.addView(Ui.text(StoreActivity.this, "Todavía no tienes objetos."));
                        for (Inventario owned : r.body()) addOwned(owned);
                    }
                    @Override public void onFailure(Call<List<Inventario>> c, Throwable t) { showConnectionError(); }
                });
            }
            @Override public void onFailure(Call<List<Objeto>> call, Throwable error) { showConnectionError(); }
        });
    }

    private void addItem(Objeto item) {
        content.addView(Ui.text(this, item.toString()));
        content.addView(Ui.text(this, item.descripcion));
        var buy = Ui.button(this, "Comprar 1"); content.addView(buy);
        buy.setEnabled(item.existencias > 0);
        buy.setOnClickListener(v -> move(item.id, false));
    }

    private void addOwned(Inventario item) {
        content.addView(Ui.text(this, item.toString()));
        var refund = Ui.button(this, "Devolver 1"); content.addView(refund);
        refund.setOnClickListener(v -> move(item.objetoId, true));
    }

    private void move(long objectId, boolean refund) {
        Movimiento request = new Movimiento(playerId, objectId, 1);
        Call<Mensaje> call = refund ? ApiClient.api().returnItem(request) : ApiClient.api().buy(request);
        call.enqueue(new Callback<>() {
            @Override public void onResponse(Call<Mensaje> c, Response<Mensaje> r) {
                if (!r.isSuccessful()) { showError(r); return; }
                Toast.makeText(StoreActivity.this, r.body().mensaje + " Saldo: " + r.body().jugador.saldo, Toast.LENGTH_SHORT).show();
                load();
            }
            @Override public void onFailure(Call<Mensaje> c, Throwable t) { showConnectionError(); }
        });
    }

    private void showError(Response<?> response) { Toast.makeText(this, Ui.errorMessage(response), Toast.LENGTH_LONG).show(); }
    private void showConnectionError() { Toast.makeText(this, "No se pudo conectar con la API.", Toast.LENGTH_LONG).show(); }
}
