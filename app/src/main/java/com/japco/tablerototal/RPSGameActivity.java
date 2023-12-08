package com.japco.tablerototal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.japco.tablerototal.model.User;
import com.japco.tablerototal.util.Dialogs;
import com.japco.tablerototal.util.SocketService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class RPSGameActivity extends AppCompatActivity {

    List<User> connectedUsers = new ArrayList<>();
    SocketService socketService;
    TextView numRondas;
    TextView player1;
    TextView player2;
    TextView resultadoPlayer1;
    TextView resultadoPlayer2;
    ImageButton rock;
    ImageButton paper;
    ImageButton scissors;
    TextView contador;
    private boolean mBound;

    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            SocketService.SocketBinder binder = (SocketService.SocketBinder) service;
            socketService = binder.getService();
            mBound = true;
            addListeners();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            socketService = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rpsgame);

        player1 = findViewById(R.id.player1);
        player2 = findViewById(R.id.player2);
        resultadoPlayer1 = findViewById(R.id.resultado1);
        resultadoPlayer2 = findViewById(R.id.resultado2);
        rock = findViewById(R.id.buttonRock);
        paper = findViewById(R.id.buttonPaper);
        scissors = findViewById(R.id.buttonScissors);
        contador = findViewById(R.id.contador);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to socket service
        Intent intent = new Intent(this, SocketService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        socketService.getSocket().off(Constants.ClientEvents.MOVE);
        socketService.getSocket().off(Constants.ServerEvents.SHOW_TIME);
        socketService.getSocket().off(Constants.ServerEvents.SHOW_TURN_RESULTS);
        socketService.getSocket().off(Constants.ServerEvents.FINISH_GAME);
        socketService.getSocket().off(Constants.ClientEvents.CLIENT_READY);

        super.onStop();
        unbindService(connection);
        mBound = false;
    }
    //Actualiza valor crónometro

    private void addListeners() {
        // Tiempo restante
        socketService.getSocket().on(Constants.ServerEvents.SHOW_TIME, args -> {
            try {
                int counter = ((JSONObject) args[0]).getInt(Constants.Keys.COUNTER);
                runOnUiThread(() -> {
                    contador.setText(counter + "s");
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        // Indicar la primera ronda al comienzo de la partida
        socketService.getSocket().on(Constants.ServerEvents.SHOW_INITIAL_INFO, args -> {
            try {
                int round = ((JSONObject) args[0]).getInt(Constants.Keys.ROUND);
                runOnUiThread(() -> {
                    numRondas.setText(String.valueOf(round)); // Corregido: convertir el valor de round a String
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        // Resultado, ronda y pts
        socketService.getSocket().on(Constants.ServerEvents.SHOW_TURN_RESULTS, args -> {
            try {
                JSONObject info = (JSONObject) args[0];
                int round = info.getInt(Constants.Keys.ROUND);
                int points = info.getInt(Constants.Keys.POINTS);
                String username = info.getString(Constants.Keys.USERNAME);
                String winnerId = info.getString(Constants.Keys.WINNER);
               // String chartData = info.getString(Constants.Keys.CHART_DATA);

                runOnUiThread(() -> {
                    player1.setText(username);
                    player2.setText(username);
                    //processChartAndPoints(chartData, points);
                    numRondas.setText(String.valueOf(round));
                    paper.setEnabled(true);
                    rock.setEnabled(true);
                    scissors.setEnabled(true);

                    // Identificar el jugador que ganó

                    updatePlayerScore(winnerId, points);
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        // Ganador partida
        socketService.getSocket().on(Constants.ServerEvents.FINISH_GAME, args -> {
            try {
                JSONObject winnerInfo = (JSONObject) args[0];
                String winnerName = winnerInfo.getString(Constants.Keys.USERNAME);

                runOnUiThread(() -> {
                    showWinner(winnerName);
                    navigateToMainMenu();
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        // Notificar al servidor cuando el cliente está listo
        socketService.getSocket().emit(Constants.ClientEvents.CLIENT_READY);
    }

    // Método para actualizar los puntos del jugador en función del ID del ganador
    private void updatePlayerScore(String winnerName, int points) {
        runOnUiThread(() -> {
            if (winnerName.equals(player1.getText().toString())) {
                // Actualizar el marcador del jugador 1
                int currentPoints = Integer.parseInt(resultadoPlayer1.getText().toString());
                resultadoPlayer1.setText(String.valueOf(currentPoints + points));
            } else if (winnerName.equals(player2.getText().toString())) {
                // Actualizar el marcador del jugador 2
                int currentPoints = Integer.parseInt(resultadoPlayer2.getText().toString());
                resultadoPlayer2.setText(String.valueOf(currentPoints + points));
            }
        });
    }

    private void showWinner(String winnerName) {
        // Inflar el diseño del diálogo
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_winner, null);
        Dialog dialog = new Dialog(this);

        // Configurar el TextView con el nombre del ganador
        TextView winnerTextView = dialogView.findViewById(R.id.textViewWinner);
        winnerTextView.setText("¡Ganador!\n" + winnerName);

        // Configurar el botón de Aceptar
        Button dismissButton = dialogView.findViewById(R.id.buttonDismiss);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cerrar el diálogo cuando se hace clic en Aceptar
                dialog.dismiss();
            }
        });

        // Crear el diálogo
        dialog.setContentView(dialogView);

        // Mostrar el diálogo
        dialog.show();
    }

    private void navigateToMainMenu() {
        // TODO: Implementar la lógica para volver a la pantalla inicial
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Opcional: cerrar esta actividad si no se desea volver a ella
    }

}