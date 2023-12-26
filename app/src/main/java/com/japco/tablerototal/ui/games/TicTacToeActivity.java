package com.japco.tablerototal.ui.games;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.japco.tablerototal.Constants;
import com.japco.tablerototal.ui.MainActivity;
import com.japco.tablerototal.MyApplication;
import com.japco.tablerototal.R;
import com.japco.tablerototal.util.Dialogs;
import com.japco.tablerototal.util.SocketService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Optional;

public class TicTacToeActivity extends AppCompatActivity {

    SocketService socketService;
    TextView[] playerNames = new TextView[2];
    TextView counter;
    Button leaveBtn;

    Player[] players = new Player[2];
    ImageView[] boardImgs = new ImageView[9];
    int[] board = new int[9];
    boolean hasTurn = false;
    String userId = null;

    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            SocketService.SocketBinder binder = (SocketService.SocketBinder) service;
            socketService = binder.getService();
            userId = socketService.getSocket().id();
            System.out.println("Bind");
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
        System.out.println("Create");
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
        leaveBtn = findViewById(R.id.buttonLeave);

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
                    int finalI = i;
                    JSONObject param = new JSONObject();
                    try {
                        param.put(Constants.Keys.CELL,finalI);
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

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("Start");
        // Bind to socket service
        Intent intent = new Intent(this, SocketService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        socketService.getSocket().off(Constants.ServerEvents.SHOW_INITIAL_INFO);
        socketService.getSocket().off(Constants.ServerEvents.NEXT_TURN);
        socketService.getSocket().off(Constants.ServerEvents.FINISH_GAME);

        // Se elimina el show_time de la sala de espera
        socketService.getSocket().off(Constants.ServerEvents.SHOW_TIME);

        socketService.getSocket().off(Constants.ServerEvents.SHOW_TIME);
        socketService.getSocket().off(Constants.ServerEvents.SHOW_TURN_RESULTS);
        unbindService(connection);
    }

    private void addSocketListeners() {
        System.out.println("Initializing TTT game listeners");
        socketService.getSocket().on(Constants.ServerEvents.SHOW_INITIAL_INFO, args -> {
            System.out.println(args[0]);
            try {
                JSONArray playersArray = ((JSONObject) args[0]).getJSONArray(Constants.Keys.PLAYERS);
                for (int i = 0; i < playersArray.length(); i++) {
                    JSONObject player = playersArray.getJSONObject(i);
                    players[i] = new Player(
                            player.getString(Constants.Keys.ID),
                            player.getString(Constants.Keys.USERNAME),
                            player.getString(Constants.Keys.SYMBOL),
                            i + 1);
                }
                runOnUiThread(() -> {
                    if (playersArray.length() >= 2) {
                        playerNames[0].setText(players[0].username);
                        playerNames[1].setText(players[1].username);
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
                System.out.println("Siguiente turno: " + nextPlayer);
                for (Player p : players) {
                    if (p.getId().equals(nextPlayer)) {
                        runOnUiThread(() -> {

                        });
                    }
                }
                if (userId.equals(nextPlayer)) {
                    this.hasTurn = true;
                    runOnUiThread(() -> Dialogs.showInfoDialog(this, "Tienes el turno", (dialog, ignore) -> {
                        dialog.dismiss();
                    }));
                } else {
                    this.hasTurn = false;
                }
                runOnUiThread(this::setBoardListeners);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        socketService.getSocket().on(Constants.ServerEvents.SHOW_TURN_RESULTS, args -> {
            System.out.println(args[0]);
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
            System.out.println(args[0]);
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

        // Notify server when client is ready
        socketService.getSocket().emit(Constants.ClientEvents.CLIENT_READY);
    }

    private void showFinishGame(JSONObject results) {
        try {
            String type = results.getString(Constants.Keys.TYPE);
            String message;
            if (type.equals(Constants.ResultTypes.DRAW)) {
                message = "¡Empate!";
            } else {
                String winner = results.getString(Constants.Keys.WINNER);
                Optional<Player> winnerP = Arrays.stream(players).filter(p -> p.getId().equals(winner))
                        .findFirst();

                if (!winnerP.isPresent())
                    return;

                String winnerUsername = winnerP.get().getUsername();
                if (winnerUsername.equals(((MyApplication) getApplication()).getUsername())) {
                    message = "¡Ganaste!\n";
                } else {
                    message = "¡Perdiste!\n";
                }
                if (type.equals(Constants.ResultTypes.TIMEOUT)) {
                    message += "Por tiempo";
                } else if (type.equals(Constants.ResultTypes.RESIGNATION)) {
                    message += "Por abandono";
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

    private class Player {
        private final String id;
        private final String username;
        private final String symbol;
        private final int number;

        public Player (String id, String username, String symbol, int number) {
            this.id = id;
            this.username = username;
            this.symbol = symbol;
            this.number = number;
        }

        public String getId() {
            return this.id;
        }

        public String getUsername() {
            return this.username;
        }

        public String getSymbol() {
            return this.symbol;
        }

        public int getNumber() {
            return this.number;
        }
    }
}
