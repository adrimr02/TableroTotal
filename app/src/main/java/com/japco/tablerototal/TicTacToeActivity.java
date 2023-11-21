package com.japco.tablerototal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.japco.tablerototal.util.Dialogs;
import com.japco.tablerototal.util.SocketService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class TicTacToeActivity extends AppCompatActivity {

    SocketService socketService;

    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            SocketService.SocketBinder binder = (SocketService.SocketBinder) service;
            socketService = binder.getService();
            addSocketListeners();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            socketService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tresenraya);
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
        socketService.getSocket().off(Constants.ServerEvents.NEXT_TURN);
        socketService.getSocket().off(Constants.ServerEvents.FINISH_GAME);
        socketService.getSocket().off(Constants.ServerEvents.SHOW_TIME);
        socketService.getSocket().off(Constants.ServerEvents.SHOW_TURN_RESULTS);
        unbindService(connection);
    }

    private void addSocketListeners() {
        socketService.getSocket().on(Constants.ServerEvents.NEXT_TURN, args -> {
            try {
                JSONObject nextPlayer = ((JSONObject) args[0]).getJSONArray(Constants.Keys.PLAYERS).getJSONObject(0);
                Log.d("NEXT_TURN", nextPlayer.getString(Constants.Keys.USERNAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        socketService.getSocket().on(Constants.ServerEvents.SHOW_TURN_RESULTS, args -> {
            try {
                int counter = ((JSONObject) args[0]).getInt(Constants.Keys.COUNTER);
                Log.d("SHOW_TIME", String.valueOf(counter));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        socketService.getSocket().on(Constants.ServerEvents.SHOW_TIME, args -> {
            try {
                int counter = ((JSONObject) args[0]).getInt(Constants.Keys.COUNTER);
                Log.d("SHOW_TIME", String.valueOf(counter));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        socketService.getSocket().on(Constants.ServerEvents.FINISH_GAME, args -> {
            try {
                int counter = ((JSONObject) args[0]).getInt(Constants.Keys.COUNTER);
                Log.d("SHOW_TIME", String.valueOf(counter));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }
}
