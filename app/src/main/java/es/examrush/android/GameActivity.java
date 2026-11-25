package es.examrush.android;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import static es.examrush.android.ApiModels.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameActivity extends AppCompatActivity {
    private static final int DURACION_SEGUNDOS = 45;
    private final Handler handler = new Handler();
    private int segundos = DURACION_SEGUNDOS;
    private int puntuacion = 0;
    private boolean enviado = false;
    private boolean enMarcha = false;
    private TextView hud;
    private GameView gameView;
    private final Runnable frame = new Runnable() { @Override public void run() { if (enMarcha && gameView != null) { gameView.step(); handler.postDelayed(this, 40); } } };
    private final Runnable clock = new Runnable() { @Override public void run() {
        if (!enMarcha) return;
        segundos--; updateHud();
        if (segundos <= 0) finishGame(); else handler.postDelayed(this, 1000);
    }};

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        if (Ui.playerId(this) < 1) {
            Toast.makeText(this, "Inicia sesión para guardar tu partida.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class)); finish(); return;
        }
        LinearLayout page = Ui.page(this);
        page.addView(Ui.text(this, "Exam Rush · ¡llega al examen!"));
        page.addView(Ui.text(this, "Toca a izquierda o derecha para moverte. Libros: +10. Distracciones: −5."));
        hud = Ui.text(this, "Tiempo: 45 s · Puntos: 0"); page.addView(hud);
        gameView = new GameView(); page.addView(gameView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
        setContentView(page);
    }

    @Override protected void onResume() { super.onResume(); if (!enviado && gameView != null) startLoop(); }
    @Override protected void onPause() { stopLoop(); super.onPause(); }
    private void startLoop() {
        if (enMarcha || enviado) return;
        enMarcha = true; handler.post(frame); handler.postDelayed(clock, 1000);
    }
    private void stopLoop() { enMarcha = false; handler.removeCallbacks(frame); handler.removeCallbacks(clock); }
    private void updateHud() { if (hud != null) hud.setText("Tiempo: " + segundos + " s · Puntos: " + puntuacion); }

    private void finishGame() {
        if (enviado) return;
        stopLoop(); enviado = true; gameView.setEnabled(false);
        long id = Ui.playerId(this);
        ApiClient.api().saveMatch(new PartidaRequest(id, puntuacion)).enqueue(new Callback<>() {
            @Override public void onResponse(Call<Partida> call, Response<Partida> response) {
                if (!response.isSuccessful()) { showFinish("Puntuación: " + puntuacion + " puntos\nNo se pudo guardar: " + Ui.errorMessage(response)); return; }
                showFinish("Puntuación: " + puntuacion + " puntos\nNota orientativa: " + response.body().notaExamen + "/10\nLa partida ya aparece en el ranking.");
            }
            @Override public void onFailure(Call<Partida> call, Throwable error) {
                showFinish("Puntuación: " + puntuacion + " puntos\nSin conexión: no se pudo guardar la partida.");
            }
        });
    }

    private void showFinish(String message) {
        new AlertDialog.Builder(this).setTitle("¡Fin de la partida!").setMessage(message)
                .setPositiveButton("Ver ranking", (dialog, which) -> startActivity(new Intent(this, RankingActivity.class)))
                .setNegativeButton("Cerrar", (dialog, which) -> finish()).setCancelable(false).show();
    }

    private final class GameView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Random random = new Random();
        private final List<FallingItem> items = new ArrayList<>();
        private float playerX = -1;
        private long lastSpawn = 0;
        private final float playerWidth = 92;
        private final float playerHeight = 76;

        GameView() { super(GameActivity.this); setBackgroundColor(Color.rgb(242, 242, 242)); }

        void step() {
            if (getWidth() < 1 || getHeight() < 1) return;
            if (playerX < 0) playerX = getWidth() / 2f;
            long now = System.currentTimeMillis();
            if (now - lastSpawn > 750) {
                lastSpawn = now;
                boolean book = random.nextInt(100) < 65;
                items.add(new FallingItem(random.nextInt(Math.max(1, getWidth() - 70)) + 35,
                        -45, book));
            }
            float playerY = getHeight() - playerHeight - 22;
            Iterator<FallingItem> iterator = items.iterator();
            while (iterator.hasNext()) {
                FallingItem item = iterator.next(); item.y += 8.0f;
                if (!item.counted && item.y + 34 >= playerY && item.y <= playerY + playerHeight
                        && Math.abs(item.x - playerX) < (playerWidth + 44) / 2f) {
                    puntuacion = Math.max(0, puntuacion + (item.book ? 10 : -5));
                    item.counted = true; iterator.remove(); updateHud(); continue;
                }
                if (item.y > getHeight() + 50) iterator.remove();
            }
            invalidate();
        }

        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth(), h = getHeight();
            paint.setColor(Color.rgb(225, 235, 225)); canvas.drawRect(0, 0, w, h, paint);
            paint.setColor(Color.rgb(170, 190, 170)); canvas.drawRect(w * .08f, 0, w * .15f, h, paint);
            canvas.drawRect(w * .85f, 0, w * .92f, h, paint);
            if (playerX < 0) playerX = w / 2f;
            float playerY = h - playerHeight - 22;
            paint.setColor(Color.rgb(50, 80, 150));
            canvas.drawRoundRect(new RectF(playerX - playerWidth / 2, playerY,
                    playerX + playerWidth / 2, playerY + playerHeight), 12, 12, paint);
            paint.setColor(Color.WHITE); paint.setTextSize(24); paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("EETAC", playerX, playerY + 45, paint);
            for (FallingItem item : items) {
                paint.setColor(item.book ? Color.rgb(180, 90, 45) : Color.rgb(100, 100, 100));
                canvas.drawRoundRect(new RectF(item.x - 27, item.y, item.x + 27, item.y + 36), 5, 5, paint);
                paint.setColor(Color.WHITE); paint.setTextSize(18);
                canvas.drawText(item.book ? "LIBRO" : "CAFÉ", item.x, item.y + 24, paint);
            }
            paint.setTextAlign(Paint.Align.LEFT); paint.setColor(Color.DKGRAY); paint.setTextSize(20);
            canvas.drawText("Toca una zona para cambiar de carril", 16, 30, paint);
        }

        @Override public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                playerX = Math.max(playerWidth / 2, Math.min(getWidth() - playerWidth / 2, event.getX()));
                invalidate(); return true;
            }
            return true;
        }
    }

    private static final class FallingItem {
        final float x; float y; final boolean book; boolean counted;
        FallingItem(float x, float y, boolean book) { this.x = x; this.y = y; this.book = book; }
    }
}
