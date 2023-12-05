package com.japco.tablerototal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.japco.tablerototal.util.SocketService;

import org.json.JSONException;
import org.json.JSONObject;

public class EvensAndNonesActivity extends AppCompatActivity {

    SocketService socketService;
    Button btPares;
    Button btNones;
    Button btSalir;
    TextView txNumero;
    TextView txTiempo;
    TextView txPuntos;
    TextView txRonda;
    String username;

    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            SocketService.SocketBinder binder = (SocketService.SocketBinder) service;
            socketService = binder.getService();

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
        txRonda = findViewById(R.id.txNumeroRonda);
        txTiempo = findViewById(R.id.txTiempoRonda);
        txPuntos = findViewById(R.id.txPuntos);

        btSalir.setOnClickListener(v -> {
            moveToMainActivity();
        });

        btNones.setOnClickListener(v -> {
            try {
                JSONObject obj  = new JSONObject();
                //TODO enviar nombre
                obj.put("numberType", "Nones");
                obj.put("number", Integer.parseInt(txNumero.getText().toString()));
                socketService.getSocket().emit(Constants.ClientEvents.MOVE, obj);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            btPares.setEnabled(false);
            btNones.setEnabled(false);
        });

        btPares.setOnClickListener(v -> {
            try {
                JSONObject obj  = new JSONObject();
                //TODO enviar nombre
                obj.put("numberType", "Pares");
                obj.put("number", Integer.parseInt(txNumero.getText().toString()));
                socketService.getSocket().emit(Constants.ClientEvents.MOVE, obj);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            btPares.setEnabled(false);
            btNones.setEnabled(false);
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

        socketService.getSocket().off(Constants.ClientEvents.MOVE);
        socketService.getSocket().off(Constants.ServerEvents.SHOW_TIME);
        socketService.getSocket().off(Constants.ServerEvents.SHOW_TURN_RESULTS);
        socketService.getSocket().off(Constants.ServerEvents.FINISH_GAME);
        socketService.getSocket().off(Constants.ClientEvents.CLIENT_READY);

        super.onStop();
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
                    txRonda.setText(round);
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        //Resultado, ronda y pts
        socketService.getSocket().on(Constants.ServerEvents.SHOW_TURN_RESULTS, args -> {
            try {
                JSONObject info = (JSONObject) args[0];
                int round = info.getInt(Constants.Keys.ROUND);

                //TODO obtener chart y obtener puntos a partir de ahi
                //TODO mostrar ranking
                //String username = info.getString(Constants.Keys.USERNAME);
                // String playerId = info.getString(Constants.Keys.PLAYER_ID);

                runOnUiThread(() -> {
                    //txPuntos.setText(points + "pts");
                    txRonda.setText(round);
                    btNones.setEnabled(true);
                    btPares.setEnabled(true);
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        //Ganador partida
        socketService.getSocket().on(Constants.ServerEvents.FINISH_GAME, args -> {
            //TODO mostrar ganador y volver a la pantalla inicial
        });

        // Notify server when client is ready
        socketService.getSocket().emit(Constants.ClientEvents.CLIENT_READY);

    }
}
