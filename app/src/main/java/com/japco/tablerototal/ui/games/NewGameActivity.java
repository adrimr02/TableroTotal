package com.japco.tablerototal.ui.games;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.japco.tablerototal.Constants;
import com.japco.tablerototal.R;
import com.japco.tablerototal.ui.MainActivity;
import com.japco.tablerototal.util.Dialogs;
import com.japco.tablerototal.util.SocketService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class NewGameActivity extends AppCompatActivity {
    private ToggleButton ticTacToeButton;
    private ToggleButton rpsButton;
    private ToggleButton oddsEvensButton;
    private SeekBar roundsSeekBar;
    private TextView roundsLabel;

    SocketService socketService;

    private String username;
    private String selectedGame = Constants.GAMES[1];
    private int selectedRounds = 3;

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
        setContentView(R.layout.activity_new_game);
        username = getIntent().getStringExtra(Constants.USERNAME_EXTRA);

        ticTacToeButton = findViewById(R.id.ticTacToeButton);
        rpsButton = findViewById(R.id.RPSButton);
        oddsEvensButton = findViewById(R.id.oddsEvensButton);
        Button backButton = findViewById(R.id.buttonBack);
        roundsSeekBar = findViewById(R.id.roundsSeekBar);
        roundsSeekBar.setProgress(selectedRounds-1);
        roundsLabel = findViewById(R.id.roundsLabel);
        roundsLabel.setText(String.format(getString(R.string.round_selection), selectedRounds));
        Button createGameButton = findViewById(R.id.buttonConfirmGame);

        ticTacToeButton.setOnClickListener(v -> checkButton(ticTacToeButton));
        rpsButton.setOnClickListener(v -> checkButton(rpsButton));
        oddsEvensButton.setOnClickListener(v -> checkButton(oddsEvensButton));
        backButton.setOnClickListener(v -> finish());
        roundsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedRounds = progress + 1;
                roundsLabel.setText(String.format(getString(R.string.round_selection), selectedRounds));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        createGameButton.setOnClickListener(v -> createGame());

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

    private void checkButton(ToggleButton checkedButton) {
        if (!checkedButton.isChecked()) {
            checkedButton.setChecked(true);
            return;
        }
        if (checkedButton == ticTacToeButton) {
            selectedGame = Constants.GAMES[1];
            oddsEvensButton.setChecked(false);
            rpsButton.setChecked(false);
            roundsLabel.setVisibility(View.GONE);
            roundsSeekBar.setVisibility(View.GONE);
        } else if (checkedButton == rpsButton) {
            selectedGame = Constants.GAMES[0];
            oddsEvensButton.setChecked(false);
            ticTacToeButton.setChecked(false);
            roundsLabel.setVisibility(View.VISIBLE);
            roundsSeekBar.setVisibility(View.VISIBLE);
        } else if (checkedButton == oddsEvensButton) {
            selectedGame = Constants.GAMES[2];
            ticTacToeButton.setChecked(false);
            rpsButton.setChecked(false);
            roundsLabel.setVisibility(View.VISIBLE);
            roundsSeekBar.setVisibility(View.VISIBLE);
        }
    }

    private void createGame() {
        if (selectedGame == null || !Arrays.asList(Constants.GAMES).contains(selectedGame))
            return;

        if (selectedRounds < 1 || selectedRounds > 5)
            return;

        JSONObject gameOptions;
        try {
            gameOptions = new JSONObject();
            gameOptions.put(Constants.GameOptions.GAME, selectedGame);
            gameOptions.put(Constants.GameOptions.ROUNDS, selectedRounds);
            gameOptions.put(Constants.GameOptions.PLAYERS, selectedGame.equals(Constants.GAMES[2]) ? 8 : 2);
            gameOptions.put(Constants.GameOptions.TIMEOUT, 10);
        } catch (JSONException e) {
            return;
        }

        if (!socketService.getSocket().connected()) {
            Dialogs.showInfoDialog(this, "No se ha podido conectar al servidor. Intentalo de nuevo.", (d, i) -> {
                d.dismiss();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            });
            return;
        }
        socketService.getSocket().emit(Constants.ClientEvents.CREATE_GAME, new Object[] { username, gameOptions } , args -> {
            JSONObject response = (JSONObject) args[0];
            boolean isError;
            String game = null;
            String roomCode = null;
            try {
                String status = response.getString(Constants.RESPONSE_STATUS);
                if (!status.equals(Constants.STATUS_OK)) {
                    isError = true;
                } else {
                    game = response.getJSONObject(Constants.GameOptions.GAME_OPTIONS).getString(Constants.GameOptions.GAME);
                    roomCode = response.getString("roomCode");
                    isError = !Arrays.asList(Constants.GAMES).contains(game);
                }
            } catch (JSONException e) {
                isError = true;
                e.printStackTrace();
            }

            if (isError) {
                runOnUiThread(() -> Dialogs.showInfoDialog(NewGameActivity.this, R.string.new_game_error_message, (DialogInterface dialog, int id) -> {
                    dialog.dismiss();
                    Intent intent = new Intent(NewGameActivity.this, MainActivity.class);
                    startActivity(intent);
                }));
            } else {
                enterWaitingRoomView(game, roomCode);
            }
        });
    }

    private void enterWaitingRoomView(String game, String roomCode) {
        Log.i("Create a new game", game);
        Intent intent = new Intent(NewGameActivity.this, WaitingRoomActivity.class);
        intent.putExtra("roomCode", roomCode);
        intent.putExtra("game", game);
        intent.putExtra("username", username);

        startActivity(intent);
    }
}
