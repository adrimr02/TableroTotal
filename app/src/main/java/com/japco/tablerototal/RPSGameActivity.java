package com.japco.tablerototal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;

import com.japco.tablerototal.util.Dialogs;
import com.japco.tablerototal.util.SocketService;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class RPSGameActivity extends AppCompatActivity {

    SocketService socketService;
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
            //addListeners();
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

    // Agrega el código para manejar el evento de jugada realizada
    private final Emitter.Listener onMoveMade = args -> {
        try {
            // Manejar evento de jugada realizada
            JSONObject moveData = (JSONObject) args[0];
            String playerId = moveData.getString("playerId");
            String move = moveData.getString("move");

            // Actualizar la interfaz de usuario según sea necesario
            runOnUiThread(() -> {


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