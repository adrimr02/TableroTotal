package com.japco.tablerototal;

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
import com.japco.tablerototal.model.User;
import com.japco.tablerototal.util.Dialogs;
import com.japco.tablerototal.util.SocketService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaitingRoomActivity extends AppCompatActivity {

    /*
        EVENTOS SERVIDOR
        1- Cronómetro (on)
        2- Actualizar jugadores conectados (on)
        3- Ponerse en estado listo (emit)
        4- Comenzar partida (emit)
     */

    List<User> connectedUsers;
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
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            SocketService.SocketBinder binder = (SocketService.SocketBinder) service;
            socketService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
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
        cronometro = findViewById(R.id.txTiempo);
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

    }

    private void compartirCodigo() {
        Intent itSend = new Intent(Intent.ACTION_SEND);
        itSend.setType("text/plain");
        itSend.putExtra(Intent.EXTRA_SUBJECT, "Te comparto el código para unirte " +
                "a mi partida: " + roomCode);

        Intent shareIntent = Intent.createChooser(itSend, null);
        startActivity(shareIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to socket service
        Intent intent = new Intent(this, SocketService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        // Añadimos eventos del servidor
        addListeners();
    }

    @Override
    protected void onStop() {
        super.onStop();
        socketService.getSocket().off(Constants.ServerEvents.SHOW_TIME);
        socketService.getSocket().off(Constants.ServerEvents.SHOW_PLAYERS_WAITING);
        socketService.getSocket().off(Constants.ClientEvents.MARK_AS_READY);
        socketService.getSocket().off(Constants.ServerEvents.START_GAME);
        unbindService(connection);
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
                int counter = ((JSONObject) args[0]).getInt(Constants.ServerEvents.SHOW_TIME);
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
                int length = ((JSONObject) args[0]).getJSONArray("players").length();
                connectedUsers.clear();

                for(int i = 0; i < length; i++){
                    JSONObject obj = ((JSONObject) args[0]).getJSONArray("players")
                            .getJSONObject(i);

                    connectedUsers.add(new User(obj.getString("username"),
                            obj.getString("readyState"), null));
                }

                runOnUiThread(() -> {
                    usersAdapter.setUsersList(connectedUsers);
                });

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        //Pasa a la partida si comienza el juego
        socketService.getSocket().on(Constants.ServerEvents.START_GAME, args -> {
            enterGameView();
        });

    }

    private void updateReadyState(){
        //Actualiza el estado del jugador (listo o no listo)
        socketService.getSocket().emit(Constants.ClientEvents.MARK_AS_READY, null, args -> {
            String text;
            text = (String) args[0];

            //Actualizamos el valor del boton al estado correspondiente
            runOnUiThread(() -> {
                listo.setText(text);
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