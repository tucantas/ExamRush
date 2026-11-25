package es.examrush.android;

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

public class RankingActivity extends AppCompatActivity {
    private LinearLayout rows;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout page = Ui.page(this); page.addView(Ui.text(this, "Ranking de Exam Rush"));
        var back = Ui.button(this, "Volver"); page.addView(back); back.setOnClickListener(v -> finish());
        ScrollView scroll = new ScrollView(this); rows = new LinearLayout(this); rows.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(rows, new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        page.addView(scroll); setContentView(page); load();
    }
    private void load() {
        rows.removeAllViews(); rows.addView(Ui.text(this, "Cargando…"));
        ApiClient.api().ranking(10).enqueue(new Callback<>() {
            @Override public void onResponse(Call<List<Ranking>> call, Response<List<Ranking>> response) {
                rows.removeAllViews();
                if (!response.isSuccessful() || response.body() == null) { Toast.makeText(RankingActivity.this, Ui.errorMessage(response), Toast.LENGTH_LONG).show(); return; }
                if (response.body().isEmpty()) rows.addView(Ui.text(RankingActivity.this, "Todavía no hay partidas."));
                for (Ranking entry : response.body()) rows.addView(Ui.text(RankingActivity.this,
                        entry.puesto + ". " + entry.nombre + " · " + entry.puntuacion + " puntos"));
            }
            @Override public void onFailure(Call<List<Ranking>> call, Throwable error) { Toast.makeText(RankingActivity.this, "No se pudo conectar con la API.", Toast.LENGTH_LONG).show(); }
        });
    }
}
