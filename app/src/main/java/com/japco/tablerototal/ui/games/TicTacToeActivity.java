package com.japco.tablerototal.ui.games;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import com.japco.tablerototal.Constants;
import com.japco.tablerototal.MyApplication;
import com.japco.tablerototal.R;
import com.japco.tablerototal.ui.MainActivity;
import com.japco.tablerototal.util.Dialogs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Optional;

public class TicTacToeActivity extends AbstractGameActivity {

    final TextView[] playerNames = new TextView[2];
    TextView counter;
    Button leaveBtn;

    final Player[] players = new Player[2];
    final ImageView[] boardImgs = new ImageView[9];
    final int[] board = new int[9];
    boolean hasTurn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tresenraya);

        findElements();

        leaveBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void findElements() {
        playerNames[0]  = findViewById(R.id.playerOneName);
        playerNames[1] = findViewById(R.id.playerTwoName);
        counter = findViewById(R.id.timeCounter);
        leaveBtn = findViewById(R.id.btSalir);

        boardImgs[0] = findViewById(R.id.image1);
        boardImgs[1] = findViewById(R.id.image2);
        boardImgs[2] = findViewById(R.id.image3);
        boardImgs[3] = findViewById(R.id.image4);
        boardImgs[4] = findViewById(R.id.image5);
        boardImgs[5] = findViewById(R.id.image6);
        boardImgs[6] = findViewById(R.id.image7);
        boardImgs[7] = findViewById(R.id.image8);
        boardImgs[8] = findViewById(R.id.image9);
    }

    private void setBoardListeners() {
        for (int i = 0; i < board.length; i++) {
            if (!hasTurn) {
                boardImgs[i].setOnClickListener(null);
                boardImgs[i].setBackground(AppCompatResources.getDrawable(this, R.drawable.gray_box));
            } else {
                boardImgs[i].setBackground(AppCompatResources.getDrawable(this, R.drawable.white_box));
                if (board[i] == 0) {
                    JSONObject param = new JSONObject();
                    try {
                        param.put(Constants.Keys.CELL, i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    boardImgs[i].setOnClickListener(v -> socketService.getSocket().emit(Constants.ClientEvents.MOVE, param));
                }
            }
        }
    }

    private void paintBoard() {
        for (int i = 0; i < board.length; i++) {
            boardImgs[i].setOnClickListener(null);
            switch (board[i]) {
                case 1:
                    boardImgs[i].setForeground(AppCompatResources.getDrawable(this, R.drawable.xtrs));
                    break;
                case 2:
                    boardImgs[i].setForeground(AppCompatResources.getDrawable(this, R.drawable.circulo));
                    break;
                default:
                    boardImgs[i].setForeground(null);
                    break;
            }
        }
    }

    protected void addSocketListeners() {
        socketService.getSocket().on(Constants.ServerEvents.SHOW_INITIAL_INFO, args -> {
            System.out.println(args[0]);
            try {
                JSONArray playersArray = ((JSONObject) args[0]).getJSONArray(Constants.Keys.PLAYERS);
                for (int i = 0; i < playersArray.length(); i++) {
                    JSONObject player = playersArray.getJSONObject(i);
                    players[i] = new Player(
                            player.getString(Constants.Keys.ID),
                            player.getString(Constants.Keys.USERNAME),
                            i + 1);
                    players[i].setSymbol(player.getString(Constants.Keys.SYMBOL));
                }
                runOnUiThread(() -> {
                    if (playersArray.length() >= 2) {
                        playerNames[0].setText(players[0].getUsername());
                        playerNames[1].setText(players[1].getUsername());
                    }
                    paintBoard();
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        socketService.getSocket().on(Constants.ServerEvents.NEXT_TURN, args -> {
            System.out.println(args[0]);
            try {
                String nextPlayer = ((JSONObject) args[0]).getJSONArray(Constants.Keys.PLAYERS).getJSONObject(0).getString(Constants.Keys.ID);
                for (Player p : players) {
                    if (p.getId().equals(nextPlayer)) {
                        runOnUiThread(() -> {

                        });
                    }
                }
                if (userId.equals(nextPlayer)) {
                    this.hasTurn = true;
                    runOnUiThread(() -> Dialogs.showInfoDialog(this, R.string.got_turn_message, (dialog, ignore) -> dialog.dismiss()));
                } else {
                    this.hasTurn = false;
                }
                runOnUiThread(this::setBoardListeners);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        socketService.getSocket().on(Constants.ServerEvents.SHOW_TURN_RESULTS, args -> {
            try {
                JSONArray board = ((JSONObject) args[0]).getJSONArray(Constants.Keys.BOARD);
                for (int i = 0; i < board.length(); i++) {
                    String cell = board.getString(i);
                    if (cell == null) {
                        this.board[i] = 0;
                    } else {
                        for (Player player : this.players) {
                            if (cell.equals(player.getId())) {
                                this.board[i] = player.getNumber();
                            }
                        }
                    }
                }
                runOnUiThread(this::paintBoard);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        socketService.getSocket().on(Constants.ServerEvents.SHOW_TIME, args -> {
            try {
                int timeLeft = ((JSONObject) args[0]).getInt(Constants.Keys.COUNTER);
                runOnUiThread(() -> counter.setText(String.valueOf(timeLeft)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        socketService.getSocket().on(Constants.ServerEvents.FINISH_GAME, args -> {
            System.out.println(args[0]);
            JSONObject results = ((JSONObject) args[0]);
            runOnUiThread(() -> showFinishGame(results));
        });
    }

    @Override
    void removeSocketListeners() {
        socketService.getSocket().off(Constants.ServerEvents.SHOW_INITIAL_INFO);
        socketService.getSocket().off(Constants.ServerEvents.NEXT_TURN);
        socketService.getSocket().off(Constants.ServerEvents.FINISH_GAME);
        socketService.getSocket().off(Constants.ServerEvents.SHOW_TIME);
        socketService.getSocket().off(Constants.ServerEvents.SHOW_TURN_RESULTS);
    }

    private void showFinishGame(JSONObject results) {
        try {
            String type = results.getString(Constants.Keys.TYPE);
            String message;
            if (type.equals(Constants.ResultTypes.DRAW)) {
                message = getString(R.string.draw_message);
            } else {
                String winner = results.getString(Constants.Keys.WINNER);
                Optional<Player> winnerP = Arrays.stream(players).filter(p -> p.getId().equals(winner))
                        .findFirst();

                if (!winnerP.isPresent())
                    return;

                String winnerUsername = winnerP.get().getUsername();
                boolean win = false;
                if (winnerUsername.equals(((MyApplication) getApplication()).getUser().getUsername())) {
                    message = getString(R.string.win_message);
                    win = true;
                } else {
                    message = getString(R.string.lose_message);
                }
                if (type.equals(Constants.ResultTypes.TIMEOUT)) {
                    message += win ? getString(R.string.win_by_time_message) : getString(R.string.lose_by_time_message);
                } else if (type.equals(Constants.ResultTypes.RESIGNATION)) {
                    message += win ? getString(R.string.win_by_resignation_message) : getString(R.string.lose_by_resignation_message);
                }
            }

            Dialogs.showInfoDialog(this, message, (dialog, which) -> {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
