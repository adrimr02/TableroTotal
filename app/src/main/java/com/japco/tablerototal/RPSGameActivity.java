package com.japco.tablerototal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
            socketService.getSocket().on(Constants.ServerEvents.MOVE_MADE, onMoveMade);
            socketService.getSocket().on(Constants.ServerEvents.ROUND_RESULT, onRoundResult);
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
        super.onStop();
        unbindService(connection);
        mBound = false;
    }
    //Actualiza valor crónometro

    private void addListeners() {

        socketService.getSocket().off(Constants.ServerEvents.SHOW_TIME);
        //Actualiza valor crónometro
        socketService.getSocket().on(Constants.ServerEvents.SHOW_TIME, args -> {
            try {
                System.out.println(args[0]);
                int counter = ((JSONObject) args[0]).getInt("counter");
                runOnUiThread(() -> {
                    contador.setText(counter + "s");
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        socketService.getSocket().on(Constants.ServerEvents.MOVE_MADE, onMoveMade); // ???? No funcionara porque el evento move_made no esta en el servidor

//        socketService.getSocket().emit(Constants.ClientEvents.)
    }

    // Agrega el código para manejar el evento de jugada realizada
    private final Emitter.Listener onMoveMade = args -> {
        try {
            // Manejar evento de jugada realizada
            JSONObject moveData = (JSONObject) args[0];
            String playerId = moveData.getString("playerId");int length = ((JSONObject) args[0]).getJSONArray("players").length();
            connectedUsers.clear();

            for(int i = 0; i < length; i++) {
                JSONObject obj = ((JSONObject) args[0]).getJSONArray("players")
                        .getJSONObject(i);
                connectedUsers.add(new User(obj.getString("username"),null, null));
            }


            // Actualizar la interfaz de usuario según sea necesario
            runOnUiThread(() -> {
                player1.setText(connectedUsers.get(0).toString());
                player2.setText(connectedUsers.get(1).toString());

            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    private final Emitter.Listener onRoundResult = args -> {
        //TODO Daba error por que no era necesario tener el try/catch
//        try {
//            // Manejar evento de resultado del round
//            JSONObject roundResult = (JSONObject) args[0];
//            // Extraer la información del resultado y actualizar la interfaz de usuario
//
//            runOnUiThread(() -> {
//            });
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    };

    private void makeMove(String move) {
        // Enviar la jugada al servidor
        try {
            JSONObject moveData = new JSONObject();
            moveData.put("playerId", "player1"); // Puedes obtener el ID del jugador según sea necesario
            moveData.put("move", move);
            socketService.getSocket().emit(Constants.ClientEvents.MOVE, moveData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}