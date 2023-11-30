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

import com.japco.tablerototal.util.Dialogs;
import com.japco.tablerototal.util.SocketService;

import org.json.JSONArray;
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
    String tipoNumero = "";
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
            tipoNumero = "Nones";
        });

        btPares.setOnClickListener(v -> {
            tipoNumero = "Pares";
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

        //TODO desactivar los .on y .emit
        socketService.getSocket().off(Constants.ServerEvents.SHOW_TIME);

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

        //Numero de ronda
        //TODO...

        //Puntuación
        //TODO...

        //Ganador partida
        //TODO...

        //Resultado de la ronda
        //TODO...

    }

    private void sendTurnInformation(){
        //TODO enviar número y tipos escogidos por jugador

    }
}
