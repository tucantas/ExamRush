package es.examrush.android;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import retrofit2.Response;

final class Ui {
    private Ui() {}
    static LinearLayout page(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundColor(Color.WHITE);
        return layout;
    }
    static TextView text(Context context, String value) {
        TextView view = new TextView(context); view.setText(value); view.setTextSize(18);
        view.setPadding(0, 12, 0, 12); return view;
    }
    static EditText input(Context context, String hint) {
        EditText field = new EditText(context); field.setHint(hint);
        field.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return field;
    }
    static Button button(Context context, String label) { Button button = new Button(context); button.setText(label); return button; }
    static String errorMessage(Response<?> response) {
        try { return new Gson().fromJson(response.errorBody().string(), ApiModels.Error.class).mensaje; }
        catch (Exception ignored) { return "Error HTTP " + response.code(); }
    }
    static long playerId(Context context) {
        return context.getSharedPreferences("examrush", Context.MODE_PRIVATE).getLong("jugadorId", -1L);
    }
}
