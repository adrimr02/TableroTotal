package com.japco.tablerototal.ui.games;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.japco.tablerototal.Constants;
import com.japco.tablerototal.ui.MainActivity;
import com.japco.tablerototal.R;
import com.japco.tablerototal.adapters.UsersListAdapter;
import com.japco.tablerototal.model.User;
import com.japco.tablerototal.util.Dialogs;
import com.japco.tablerototal.util.SocketService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WaitingRoomActivity extends AppCompatActivity {

    /*
        EVENTOS SERVIDOR
        1- Cronómetro (on)
        2- Actualizar jugadores conectados (on)
        3- Ponerse en estado listo (emit)
        4- Comenzar partida (emit)

        5- Abandonar partida
        6- Avisar cuando no haya suficientes jugadores
     */

    List<User> connectedUsers = new ArrayList<>();
    UsersListAdapter usersAdapter;
    String roomCode;
    String game;
    RecyclerView connectedUsersView;
    Button volver;
    Button listo;
    FloatingActionButton compartir;
    SocketService socketService;
    TextView cronometro;
    TextView codigo;
    boolean mBound;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            SocketService.SocketBinder binder = (SocketService.SocketBinder) service;
            socketService = binder.getService();
            mBound = true;

            // Añadimos eventos del servidor
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
        setContentView(R.layout.activity_waiting_room);

        this.roomCode = getIntent().getStringExtra("roomCode");
        this.game = getIntent().getStringExtra("game");

        volver = findViewById(R.id.btAtras);
        connectedUsersView = (RecyclerView)findViewById(R.id.rcylConnectedUsers);
        cronometro = findViewById(R.id.txContador);
        listo = findViewById(R.id.btListo);
        codigo = findViewById(R.id.txCodigo);
        compartir = findViewById(R.id.fabCompartir);

        connectedUsersView.setHasFixedSize(true);
        codigo.setText(roomCode);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        connectedUsersView.setLayoutManager(layoutManager);
        usersAdapter= new UsersListAdapter(connectedUsers,
                new UsersListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(User item) {
                        clickOnItem(item);
                    }
                });
        connectedUsersView.setAdapter(usersAdapter);

        listo.setOnClickListener(v -> {
            updateReadyState();
        });

        compartir.setOnClickListener(v -> {
            compartirCodigo();
        });

        volver.setOnClickListener(v -> {
            moveToMainActivity();
        });

    }

    private void moveToMainActivity() {
        Intent intent = new Intent(WaitingRoomActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void compartirCodigo() {
        Intent itSend = new Intent(Intent.ACTION_SEND);
        itSend.setType("text/plain");
        itSend.putExtra(Intent.EXTRA_SUBJECT, "¿A qué esperas? ¡Únete a mi partida!");
        itSend.putExtra(Intent.EXTRA_TEXT, "¡Te reto a una partida de " + game + "! " +
                "El código de la sala es: " + roomCode);

        Intent shareIntent = Intent.createChooser(itSend, null);
        startActivity(shareIntent);
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
        System.out.println("Removing WR listeners");
        socketService.getSocket().off(Constants.ServerEvents.SHOW_PLAYERS_WAITING);
        socketService.getSocket().off(Constants.ClientEvents.MARK_AS_READY);
        socketService.getSocket().off(Constants.ServerEvents.START_GAME);
        unbindService(connection);
        mBound = false;
    }

    public void clickOnItem(User user){
        Log.i("Click adapter","Item Clicked "+user.getNickname());
        //Toast.makeText(MainActivity.this, "Item Clicked "+user.getId(), Toast.LENGTH_LONG).show();
        //Intent intent=new Intent (MainRecycler.this, MainActivity.class);
        //intent.putExtra(MATCH_SELECTED, match);
        //Poner algo de transacciones ???
        //startActivity(intent);
    }

    private void addListeners(){

        //Actualiza valor crónometro
        socketService.getSocket().on(Constants.ServerEvents.SHOW_TIME, args -> {
            try {
                System.out.println(args[0]);
                int counter = ((JSONObject) args[0]).getInt("counter");
                runOnUiThread(() -> {
                    cronometro.setText(counter + "s");
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        //Actualiza lista de usuarios conectados
        socketService.getSocket().on(Constants.ServerEvents.SHOW_PLAYERS_WAITING, args -> {
            try {
                System.out.println(args[0]);

                int length = ((JSONObject) args[0]).getJSONArray("players").length();
                connectedUsers.clear();

                for(int i = 0; i < length; i++){
                    JSONObject obj = ((JSONObject) args[0]).getJSONArray("players")
                            .getJSONObject(i);

                    String state;
                    if(obj.getString("readyState").equals(Constants.READY))
                        state = getString(R.string.ready);
                    else
                        state = getString(R.string.not_ready);

                    connectedUsers.add(new User(obj.getString("username"),
                            state, null));
                }

                runOnUiThread(() -> {
                    usersAdapter.setUsersList(connectedUsers);
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        //Pasa a la partida si comienza el juego
        socketService.getSocket().on(Constants.ServerEvents.START_GAME, args -> {
            enterGameView();
        });

        //Cancela si no se cumplen las condiciones para iniciar partida
        socketService.getSocket().on("error", args -> {
            try {
                String errorMsg = ((JSONObject) args[0]).getString("code");
                if(errorMsg.equals(Constants.NOT_ENOUGHT_PLAYERS)){
                    runOnUiThread(() -> {
                        Dialogs.showInfoDialog(WaitingRoomActivity.this,
                                getString(R.string.not_enough_players),
                                (DialogInterface dialog, int id) -> {
                            moveToMainActivity();
                        });
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateReadyState(){
        //Actualiza el estado del jugador (listo o no listo)
        socketService.getSocket().emit(Constants.ClientEvents.MARK_AS_READY, null, args -> {
            String text;
            text = (String) args[0];

            //Actualizamos el valor del boton al siguiente estado
            runOnUiThread(() -> {
                if(text.equals(Constants.NOT_READY)){
                    listo.setText(getString(R.string.ready));
                } else {
                    listo.setText(getString(R.string.not_ready));
                }
            });
        });
    }

    private void enterGameView() {
        Log.i("Joining game", game);
        Intent intent;
        if (game.equals(Constants.GAMES[0])) {
            intent = new Intent(WaitingRoomActivity.this, RPSGameActivity.class);
        } else if (game.equals(Constants.GAMES[1])) {
            intent = new Intent(WaitingRoomActivity.this, TicTacToeActivity.class);
        } else if (game.equals(Constants.GAMES[2])) {
            intent = new Intent(WaitingRoomActivity.this, EvensAndNonesActivity.class);
        } else {
            return;
        }
        startActivity(intent);
    }
}