package com.japco.tablerototal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.japco.tablerototal.util.Dialogs;
import com.japco.tablerototal.util.SocketService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import io.socket.client.Socket;

public class JoinGameActivity extends AppCompatActivity {

    private Button joinButton;
    private EditText codeField;

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
        setContentView(R.layout.activity_join_game);

        joinButton = findViewById(R.id.buttonJoin);
        codeField = findViewById(R.id.gameCodeField);
        TextView textUsername = findViewById(R.id.textUsername);

        String username = getIntent().getStringExtra(Constants.USERNAME_EXTRA);
        textUsername.setText(username);

        joinButton.setOnClickListener(v -> {
            if (codeField.getText().toString().trim().length() != 6) {
                Dialogs.showInfoDialog(JoinGameActivity.this, R.string.no_code_dialog_message, (DialogInterface dialog, int id) -> {
                    dialog.dismiss();
                });
                return;
            }
            connect(codeField.getText().toString().trim(), username);
        });

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
        socketService.getSocket().emit("join", new String[] { username, roomCode} , args -> {
            JSONObject response = (JSONObject) args[0];
            boolean isError = false;
            String game = null;
            try {
                String status = response.getString(Constants.RESPONSE_STATUS);
                if (!status.equals(Constants.STATUS_OK)) {
                    isError = true;
                } else {
                    game = response.getJSONObject(Constants.GameOptions.GAME_OPTIONS).getString(Constants.GameOptions.GAME);
                    isError = !Arrays.asList(Constants.GAMES).contains(game);
                }
            } catch (JSONException e) {
                isError = true;
            }

            if (isError) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialogs.showInfoDialog(JoinGameActivity.this, R.string.join_error_message, (DialogInterface dialog, int id) -> {
                            dialog.dismiss();
                            Intent intent = new Intent(JoinGameActivity.this, MainActivity.class);
                            startActivity(intent);
                        });
                    }
                });
            } else {
                enterGameView(game, socketService);
            }
        });
    }

    private void enterGameView(String game, SocketService mSocket) {
        Log.i("Joining game", game);
        Intent intent = null;
        if (game.equals(Constants.GAMES[0])) {
            intent = new Intent(JoinGameActivity.this, RPSGameActivity.class);
        } else if (game.equals(Constants.GAMES[1])) {
            intent = new Intent(JoinGameActivity.this, TicTacToeActivity.class);
        } else if (game.equals(Constants.GAMES[2])) {
            intent = new Intent(JoinGameActivity.this, EvensAndNonesActivity.class);
        } else {
            return;
        }
        startActivity(intent);
    }
}