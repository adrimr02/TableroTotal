package com.japco.tablerototal;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.japco.tablerototal.util.Dialogs;
import com.japco.tablerototal.util.SocketService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class NewGameActivity extends AppCompatActivity {
    private Button ticTacToeButton;
    private Button rpsButton;
    private Button evensAndNonesButton;
    private Button backButton;

    SocketService socketService;
    private boolean mBound;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            SocketService.SocketBinder binder = (SocketService.SocketBinder) service;
            socketService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        ticTacToeButton = findViewById(R.id.tictactoeButton);
        rpsButton = findViewById(R.id.RPSButton);
        evensAndNonesButton = findViewById(R.id.EvensAndNonesButton);
        backButton = findViewById(R.id.buttonBack);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind socket to service
        Intent intent = new Intent(this, SocketService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;
    }

    private void connect(String roomCode, String username) {
        socketService.getSocket().emit("create", new String[] { username, roomCode} , args -> {
            JSONObject response = (JSONObject) args[0];
            boolean isError = false;
            String game = null;
            try {
                String status = response.getString("status");
                game = response.getString("game");

                isError = !status.equals("ok");
                isError |= !Arrays.asList(Constants.GAMES).contains(game);
            } catch (JSONException e) {
                isError = true;
            }

            if (isError) {
                Dialogs.showInfoDialog(NewGameActivity.this, R.string.new_game_error_message, (DialogInterface dialog, int id) -> {
                    dialog.dismiss();
                    Intent intent = new Intent(NewGameActivity.this, MainActivity.class);
                    startActivity(intent);
                });
            } else {
                enterGameView(game, socketService);
            }
        });
    }

    private void enterGameView(String game, SocketService mSocket) {
        Log.i("Create a new game", game);
        Intent intent = null;
        if (game.equals(Constants.GAMES[0])) {
            intent = new Intent(NewGameActivity.this, RPSGameActivity.class);
        } else if (game.equals(Constants.GAMES[1])) {
             intent = new Intent(NewGameActivity.this, TicTacToeActivity.class);
        } else if (game.equals(Constants.GAMES[2])) {
             intent = new Intent(NewGameActivity.this, EvensAndNonesActivity.class);
        } else {
            return;
        }
        startActivity(intent);
    }
}
