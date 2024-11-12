package com.example.juegodeprobabilidad2;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private int nivel = 1;
    private int puntos = 0;
    private int maxPuntos = 0;
    private LinearLayout cardContainer;
    private TextView scoreText, messageText, recordText, levelText;
    private boolean cartaSeleccionada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreText = findViewById(R.id.scoreText);
        messageText = findViewById(R.id.messageText);
        recordText = findViewById(R.id.recordText);
        levelText = findViewById(R.id.levelText);
        cardContainer = findViewById(R.id.cardContainer);

        SharedPreferences prefs = getSharedPreferences("MisDatos", MODE_PRIVATE);
        maxPuntos = prefs.getInt("maxPuntos", 0);

        recordText.setText(getString(R.string.record, maxPuntos));
        levelText.setText(getString(R.string.nivel, nivel));
        scoreText.setText(getString(R.string.puntuacion, puntos));

        actualizarNivel();
    }

    private void actualizarNivel() {
        cartaSeleccionada = false;
        levelText.setText(getString(R.string.nivel, nivel));
        cardContainer.removeAllViews();

        int totalCartas = nivel + 1;
        int cartasBuenas = getCartasBuenas(nivel);

        ArrayList<Boolean> cartas = new ArrayList<>();

        for (int i = 0; i < cartasBuenas; i++) {
            cartas.add(true);
        }
        for (int i = 0; i < totalCartas - cartasBuenas; i++) {
            cartas.add(false);
        }

        Collections.shuffle(cartas);

        for (int i = 0; i < totalCartas; i++) {
            ImageView carta = new ImageView(this);
            carta.setLayoutParams(new LinearLayout.LayoutParams(200, 300));
            carta.setImageResource(R.drawable.carta_reverso);
            carta.setTag(cartas.get(i));

            carta.setOnClickListener(view -> {
                if (!cartaSeleccionada) {
                    cartaSeleccionada = true;

                    if ((boolean) view.getTag()) {
                        carta.setImageResource(R.drawable.carta_x);
                        puntos += nivel * 100;
                        scoreText.setText(getString(R.string.puntuacion, puntos));
                        playSound(true);
                        view.setClickable(false);

                        messageText.setVisibility(View.VISIBLE);
                        messageText.setText("¡Has acertado! Siguiente nivel:");

                        new Handler().postDelayed(() -> {
                            messageText.setVisibility(View.GONE);
                            if (nivel < 10) {
                                nivel++;
                                actualizarNivel();
                            } else {
                                terminarJuego(true);
                            }
                        }, 1000);
                    } else {
                        carta.setImageResource(R.drawable.carta_blanca);
                        playSound(false);
                        terminarJuego(false);
                    }
                }
            });

            cardContainer.addView(carta);
        }
    }

    private void playSound(boolean acierto) {
        MediaPlayer mp = MediaPlayer.create(this, acierto ? R.raw.coin : R.raw.sad);
        mp.start();
        mp.setOnCompletionListener(MediaPlayer::release);
    }

    private void terminarJuego(boolean ganado) {
        if (ganado) {
            messageText.setText("¡Felicidades, has completado el juego!");
        } else {
            messageText.setText("Game Over");
        }

        if (puntos > maxPuntos) {
            maxPuntos = puntos;
            recordText.setText(getString(R.string.record, maxPuntos));
            SharedPreferences.Editor editor = getSharedPreferences("MisDatos", MODE_PRIVATE).edit();
            editor.putInt("maxPuntos", maxPuntos);
            editor.apply();
        }

        new AlertDialog.Builder(this)
                .setMessage(ganado ? "¡Juego Completado! ¿Quieres jugar de nuevo?" : "Game Over. ¿Quieres jugar de nuevo?")
                .setPositiveButton("Sí", (dialog, which) -> reiniciarJuego())
                .setNegativeButton("Salir", (dialog, which) -> finish())
                .show();
    }

    private void reiniciarJuego() {
        nivel = 1;
        puntos = 0;
        cartaSeleccionada = false;
        scoreText.setText(getString(R.string.puntuacion, puntos));
        actualizarNivel();
    }

    private int getCartasBuenas(int nivel) {
        switch (nivel) {
            case 1: return 1;
            case 2: return 2;
            case 3: return 2;
            case 4: return 3;
            case 5: return 3;
            case 6: return 4;
            case 7: return 4;
            case 8: return 5;
            case 9: return 5;
            case 10: return 6;
            default: return 1;
        }
    }
}
