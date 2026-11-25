package es.examrush.android;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout page = Ui.page(this); page.addView(Ui.text(this, "Exam Rush · EETAC"));
        page.addView(Ui.text(this, "Semana de exámenes: recoge libros y evita objetos que restan puntos."));
        var register = Ui.button(this, "Registro"); page.addView(register);
        var login = Ui.button(this, "Login"); page.addView(login);
        var store = Ui.button(this, "Tienda"); page.addView(store);
        var game = Ui.button(this, "Jugar partida"); page.addView(game);
        var ranking = Ui.button(this, "Ranking"); page.addView(ranking);
        var profile = Ui.button(this, "Mi perfil e historial"); page.addView(profile);
        register.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        login.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        store.setOnClickListener(v -> startActivity(new Intent(this, StoreActivity.class)));
        game.setOnClickListener(v -> startActivity(new Intent(this, GameActivity.class)));
        ranking.setOnClickListener(v -> startActivity(new Intent(this, RankingActivity.class)));
        profile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        setContentView(page);
    }
}
