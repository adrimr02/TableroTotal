package com.japco.tablerototal.ui.games;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.japco.tablerototal.Constants;
import com.japco.tablerototal.R;
import com.japco.tablerototal.ui.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RPSGameActivity extends AbstractGameActivity {

    TextView numRondas;
    final TextView[] playerNames = new TextView[2];
    final Player[] players = new Player[2];
    TextView resultadoPlayer1;
    TextView resultadoPlayer2;
    ImageButton rock;
    ImageButton paper;
    ImageButton scissors;
    TextView contador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rpsgame);

        playerNames[0]  = findViewById(R.id.player1);
        playerNames[1] = findViewById(R.id.player2);
        resultadoPlayer1 = findViewById(R.id.resultado1);
        resultadoPlayer2 = findViewById(R.id.resultado2);
        rock = findViewById(R.id.buttonRock);
        paper = findViewById(R.id.buttonPaper);
        scissors = findViewById(R.id.buttonScissors);
        contador = findViewById(R.id.contador);
        numRondas = findViewById(R.id.numRondas);

        // Agregar OnClickListener a los botones
        rock.setOnClickListener(v -> onButtonClick("rock"));
        paper.setOnClickListener(v -> onButtonClick("paper"));
        scissors.setOnClickListener(v -> onButtonClick("scissors"));

        Button leaveGameButton = findViewById(R.id.buttonLeave);
        leaveGameButton.setOnClickListener(v -> {
            // Lógica para navegar hacia atrás
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void onButtonClick(String move) {
        // Desactivar los botones después de hacer clic
        rock.setEnabled(false);
        paper.setEnabled(false);
        scissors.setEnabled(false);

        // Enviar el movimiento al servidor
        socketService.getSocket().emit(Constants.ClientEvents.MOVE, move);
    }

    @Override
    protected void onShowInitialInfo(Object[] args) {
        try {
            JSONArray playersArray = ((JSONObject) args[0]).getJSONArray(Constants.Keys.PLAYERS);
            for (int i = 0; i < playersArray.length(); i++) {
                JSONObject player = playersArray.getJSONObject(i);
                players[i] = new Player(
                        player.getString(Constants.Keys.ID),
                        player.getString(Constants.Keys.USERNAME),
                        i + 1);
            }
            runOnUiThread(() -> {
                if (playersArray.length() >= 2) {
                    playerNames[0].setText(players[0].getUsername());
                    playerNames[1].setText(players[1].getUsername());
                }
                enableButtons();

            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onTurnResults(Object[] args) {
        try {
            JSONObject info = (JSONObject) args[0];
            System.out.println(info);

            int round = info.getInt(Constants.Keys.ROUND) + 2;

            // Obtén los puntos de cada jugador del objeto 'points'
            JSONObject pointsObject = info.getJSONObject(Constants.Keys.POINTS);
            Map<String, Integer> playerPoints = new HashMap<>();
            Iterator<String> keys = pointsObject.keys();
            while (keys.hasNext()) {
                String playerId = keys.next();
                int points = pointsObject.isNull(playerId) ? 0 : pointsObject.getInt(playerId);
                playerPoints.put(playerId, points);
            }

            runOnUiThread(() -> {
                // Update round number
                numRondas.setText(String.valueOf(round));

                // Enable buttons after round results are shown
                paper.setEnabled(true);
                rock.setEnabled(true);
                scissors.setEnabled(true);

                // Iterate through the playerPoints map and update player scores
                for (Map.Entry<String, Integer> entry : playerPoints.entrySet()) {
                    updatePlayerScore(entry.getKey(), entry.getValue());
                }

                System.out.println(info.optString(Constants.Keys.WINNER));
//              showRoundWinner(info.optString(Constants.Keys.WINNER)); // Use the actual winner ID from the response
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onShowTime(Object[] args) {
        try {
            int counter = ((JSONObject) args[0]).getInt(Constants.Keys.COUNTER);
            runOnUiThread(() -> contador.setText(counter + "s"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onFinishGame(Object[] args) {
        try {
            JSONObject winnerInfo = (JSONObject) args[0];

            // Verificar si la propiedad "username" está presente en el objeto
            if (winnerInfo.has(Constants.Keys.WINNER)) {
                String winnerId = winnerInfo.getString(Constants.Keys.WINNER);
                String winnerName = null;
                for (Player player : players) {
                    if (player.getId().equals(winnerId)) {
                        winnerName = player.getUsername();
                    }
                }
                String finalWinnerName = winnerName;
                runOnUiThread(() -> showWinner(finalWinnerName));
            } else {
                // Manejar el caso en el que "username" no está presente
                // Puedes imprimir un mensaje de error o realizar otra acción según tus necesidades
                System.err.println("El objeto winnerInfo no contiene la propiedad 'username'");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void enableButtons() {
        runOnUiThread(() -> {
            rock.setEnabled(true);
            paper.setEnabled(true);
            scissors.setEnabled(true);
        });
    }

//    private void showRoundWinner(String winnerId) {
//        for (Player player : players) {
//            if (player.getId().equals(winnerId)) {
//                // Inflating the winner message view from dialog_winner_round.xml
//                View winnerMessageView = LayoutInflater.from(this).inflate(R.layout.dialog_winner, null);
//                TextView winnerMessageTextView = winnerMessageView.findViewById(R.id.textViewWinner);
//                winnerMessageTextView.setText("¡" + player.getUsername() + " ganó la ronda!");
//
//                // Creating and showing the dialog
//                Dialog winnerRoundDialog = new Dialog(this);
//                winnerRoundDialog.setContentView(winnerMessageView);
//                winnerRoundDialog.show();
//                break;
//            }
//        }
//    }

    // Método para actualizar los puntos del jugador en función del ID del ganador
    private void updatePlayerScore(String winnerId, int points) {
        // Registro para comprobar si se llama al método
        System.out.println("updatePlayerScore llamado con winnerId: " + winnerId + ", points: " + points);

        // Actualizar los puntos del jugador según el ID del ganador
        for (Player player : players) {
            if (player.getId().equals(winnerId)) {
                player.setPoints(points);
                break;
            }
        }

        // Actualizar la interfaz de usuario con los nuevos puntos
        runOnUiThread(() -> {
            resultadoPlayer1.setText(String.valueOf(players[0].getPoints()));
            resultadoPlayer2.setText(String.valueOf(players[1].getPoints()));
        });
    }

    private void showWinner(String winnerName) {
        // Inflar el diseño del diálogo
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_winner, null);
        Dialog dialog = new Dialog(this);

        // Configurar el TextView con el nombre del ganador
        TextView winnerTextView = dialogView.findViewById(R.id.textViewWinner);
        if (winnerName != null) {
            winnerTextView.setText(getString(R.string.winner_dialog, winnerName));
        } else {
            winnerTextView.setText(R.string.draw_dialog);
        }

        // Configurar el botón de Aceptar
        Button dismissButton = dialogView.findViewById(R.id.buttonDismiss);
        dismissButton.setOnClickListener(v -> {
            // Cerrar el diálogo cuando se hace clic en Aceptar
            dialog.dismiss();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        // Crear el diálogo
        dialog.setContentView(dialogView);

        // Mostrar el diálogo
        dialog.show();
    }
}
