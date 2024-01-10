package com.japco.tablerototal.ui.games;

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

import androidx.appcompat.app.AppCompatActivity;

import com.japco.tablerototal.Constants;
import com.japco.tablerototal.MyApplication;
import com.japco.tablerototal.R;
import com.japco.tablerototal.model.AuthUser;
import com.japco.tablerototal.ui.MainActivity;
import com.japco.tablerototal.util.Dialogs;
import com.japco.tablerototal.util.SocketService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class JoinGameActivity extends AppCompatActivity {

    private EditText codeField;

    SocketService socketService;

    AuthUser authUser;

    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            SocketService.SocketBinder binder = (SocketService.SocketBinder) service;
            socketService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            socketService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        authUser = ((MyApplication) getApplication()).getUser();

        Button joinButton = findViewById(R.id.buttonJoin);
        codeField = findViewById(R.id.gameCodeField);
        TextView textUsername = findViewById(R.id.textUsername);

        textUsername.setText(authUser.getUsername());

        joinButton.setOnClickListener(v -> {
            if (codeField.getText().toString().trim().length() != 6) {
                Dialogs.showInfoDialog(JoinGameActivity.this, R.string.no_code_dialog_message, (DialogInterface dialog, int id) -> dialog.dismiss());
                return;
            }
            connect(codeField.getText().toString().trim(), authUser.getUsername());
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
    }

    private void connect(String roomCode, String username) {
        if (!socketService.getSocket().connected()) {
            Dialogs.showInfoDialog(this, R.string.server_offline, (d, i) -> {
                d.dismiss();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            });
            return;
        }
        JSONObject user = new JSONObject();
        try {
            user.put("userId", authUser.getUserId());
            user.put("username", authUser.getUsername());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        socketService.getSocket().emit(Constants.ClientEvents.JOIN_GAME, new Object[] { user, roomCode} , args -> {
            JSONObject response = (JSONObject) args[0];
            boolean isError;
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
                runOnUiThread(() -> Dialogs.showInfoDialog(JoinGameActivity.this,
                        R.string.join_error_message, (DialogInterface dialog, int id) -> dialog.dismiss()));
            } else {
                enterGameView(game, roomCode);
            }
        });
    }

    private void enterGameView(String game, String roomCode) {
        Log.i("Joining game", game);
        Log.i("Create a new game", game);
        Intent intent = new Intent(JoinGameActivity.this, WaitingRoomActivity.class);
        intent.putExtra("roomCode", roomCode);
        intent.putExtra("game", game);

        startActivity(intent);
    }
}