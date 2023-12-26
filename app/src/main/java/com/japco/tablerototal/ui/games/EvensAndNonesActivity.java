package com.japco.tablerototal.ui.games;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.japco.tablerototal.R;
import com.japco.tablerototal.util.Dialogs;
import com.japco.tablerototal.util.SocketService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EvensAndNonesActivity extends AppCompatActivity {

    //TODO gestionar empates

    SocketService socketService;
    Button btPares;
    Button btNones;
    Button btSalir;
    TextView txNumero;
    TextView txTiempo;
    TextView txPuntos;
    TextView txRonda;
    TextView[] ranking;
    TextView txRanking;
    String username;
    String userId;

    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            SocketService.SocketBinder binder = (SocketService.SocketBinder) service;
            socketService = binder.getService();
            userId = socketService.getSocket().id();

            addListeners();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            socketService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paresynones);

        username = getIntent().getStringExtra("username");

        btNones = findViewById(R.id.botonNones);
        btPares =  findViewById(R.id.botonPares);
        btSalir = findViewById(R.id.btSalir);
        txNumero = findViewById(R.id.txNumero);
        txRonda = findViewById(R.id.txNumeroRondaOE);
        txTiempo = findViewById(R.id.txTiempoRonda);
        txPuntos = findViewById(R.id.txPuntos);
        ranking = new TextView[]{
                findViewById(R.id.txTop1),
                findViewById(R.id.txTop2),
                findViewById(R.id.txTop3)
        };
        txRanking = findViewById(R.id.txRanking);

        btSalir.setOnClickListener(v -> {
            moveToMainActivity();
        });

        //Enviamos la repuesta del jugador, su identificación la posee el servidor
        btNones.setOnClickListener(v -> {
            if(!txNumero.getText().toString().equals("")) {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("numberType", "nones");
                    obj.put("number", Integer.parseInt(txNumero.getText().toString()));
                    socketService.getSocket().emit(Constants.ClientEvents.MOVE, obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Desactivamos los botones para evitar más respuestas
                btPares.setEnabled(false);
                btNones.setEnabled(false);
                txNumero.setEnabled(false);
            }
        });

        btPares.setOnClickListener(v -> {
            if(!txNumero.getText().toString().equals("")){
                try {
                    JSONObject obj  = new JSONObject();
                    obj.put("numberType", "evens");
                    obj.put("number", Integer.parseInt(txNumero.getText().toString()));
                    socketService.getSocket().emit(Constants.ClientEvents.MOVE, obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Desactivamos los botones para evitar más respuestas
                btPares.setEnabled(false);
                btNones.setEnabled(false);
                txNumero.setEnabled(false);
            }
        });
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(EvensAndNonesActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
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
        super.onStop();

        socketService.getSocket().off(Constants.ServerEvents.SHOW_TIME);
        socketService.getSocket().off(Constants.ServerEvents.SHOW_INITIAL_INFO);
        socketService.getSocket().off(Constants.ServerEvents.SHOW_TURN_RESULTS);
        socketService.getSocket().off(Constants.ServerEvents.FINISH_GAME);

        unbindService(connection);
    }

    private void addListeners(){
        //Tiempo restante
        socketService.getSocket().on(Constants.ServerEvents.SHOW_TIME, args -> {
            try {
                int counter = ((JSONObject) args[0]).getInt(Constants.Keys.COUNTER);
                runOnUiThread(() -> {
                    txTiempo.setText(counter + "s");
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        //Indicar la primera ronda al comienzo de la partida
        socketService.getSocket().on(Constants.ServerEvents.SHOW_INITIAL_INFO, args -> {
            try {
                int round = ((JSONObject) args[0]).getInt(Constants.Keys.ROUND);
                runOnUiThread(() -> {
                    txRonda.setText(String.valueOf(round));
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        //Obtener siguiente ronda y puntos del jugador
        socketService.getSocket().on(Constants.ServerEvents.SHOW_TURN_RESULTS, args -> {
            int points = 0;

            try {
                JSONObject info = (JSONObject) args[0];

                int round = info.getInt(Constants.Keys.ROUND);
                JSONArray chart = info.getJSONArray(Constants.Keys.CHART);

                for(int i = 0; i < chart.length(); i++){
                    JSONObject obj = chart.getJSONObject(i);
                    String id = obj.getString(Constants.Keys.PLAYER_ID);
                    if(id.equals(userId)){
                        points = obj.getInt(Constants.Keys.POINTS);
                        break;
                    }
                }

                int finalPoints = points;

                for(int i = 0; i < chart.length(); i++) {
                    if (i == 3) {
                        break;
                    }

                    JSONObject obj = chart.getJSONObject(i);
                    String topUsername = obj.getString(Constants.Keys.USERNAME);
                    int topPoints = obj.getInt(Constants.Keys.POINTS);

                    String message = (i + 1) + ". " + topUsername + " (" + topPoints + "pts)";
                    TextView tx = ranking[i];
                    runOnUiThread(() -> {
                        tx.setText(message);
                        tx.setVisibility(View.VISIBLE);
                    });
                }

                runOnUiThread(() -> {
                    txPuntos.setText(finalPoints + "pts");
                    txRonda.setText(String.valueOf(round));
                    btNones.setEnabled(true);
                    btPares.setEnabled(true);
                    txNumero.setEnabled(true);
                    txNumero.setText("");

                    if(chart.length() > 0){
                        txRanking.setVisibility(View.VISIBLE);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        //Ganador partida
        socketService.getSocket().on(Constants.ServerEvents.FINISH_GAME, args -> {
            try {
                JSONObject obj = (JSONObject) args[0];
                String winner = obj.getString(Constants.Keys.WINNER);
                int winnerPoints = obj.getInt(Constants.Keys.POINTS);

                String message = "¡" + winner + " ha ganado la partida con "
                        + winnerPoints + " puntos!";

                runOnUiThread(() -> {
                    Dialogs.showInfoDialog(this, message, (dialog, which) -> {
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    });
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        // Notify server when client is ready
        socketService.getSocket().emit(Constants.ClientEvents.CLIENT_READY);

    }
}