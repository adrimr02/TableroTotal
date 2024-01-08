package com.japco.tablerototal.ui.games;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;

import com.japco.tablerototal.Constants;
import com.japco.tablerototal.MyApplication;
import com.japco.tablerototal.model.AuthUser;
import com.japco.tablerototal.ui.MainActivity;
import com.japco.tablerototal.util.Dialogs;
import com.japco.tablerototal.util.SocketService;

public abstract class AbstractGameActivity extends AppCompatActivity {

    protected SocketService socketService;

    AuthUser authUser;

    protected String socketId;

    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            SocketService.SocketBinder binder = (SocketService.SocketBinder) service;
            socketService = binder.getService();
            socketId = socketService.getSocket().id();
            addSocketListeners();
            // Notify server when client is ready
            socketService.getSocket().emit(Constants.ClientEvents.CLIENT_READY);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            socketService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authUser = ((MyApplication) getApplication()).getUser();
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
        removeSocketListeners();
        unbindService(connection);
    }

    protected void addSocketListeners() {
        // Make sure waiting room show time listener is removed
        socketService.getSocket().off(Constants.ServerEvents.SHOW_TIME);

        socketService.getSocket().on(Constants.ServerEvents.SHOW_INITIAL_INFO, this::onShowInitialInfo);
        socketService.getSocket().on(Constants.ServerEvents.NEXT_TURN, this::onNextTurn);
        socketService.getSocket().on(Constants.ServerEvents.SHOW_TURN_RESULTS, this::onTurnResults);
        socketService.getSocket().on(Constants.ServerEvents.SHOW_TIME, this::onShowTime);
        socketService.getSocket().on(Constants.ServerEvents.FINISH_GAME, this::onFinishGame);

        socketService.getSocket().on(Constants.ServerEvents.DISCONNECT, this::onDisconnect);

        socketService.getSocket().emit(Constants.ClientEvents.CLIENT_READY);
    }
    protected void removeSocketListeners() {
        socketService.getSocket().off(Constants.ServerEvents.SHOW_INITIAL_INFO);
        socketService.getSocket().off(Constants.ServerEvents.NEXT_TURN);
        socketService.getSocket().off(Constants.ServerEvents.FINISH_GAME);
        socketService.getSocket().off(Constants.ServerEvents.SHOW_TIME);
        socketService.getSocket().off(Constants.ServerEvents.SHOW_TURN_RESULTS);
        socketService.getSocket().off(Constants.ServerEvents.DISCONNECT);
    }

    protected void onShowInitialInfo(Object[] args) {}

    protected void onNextTurn(Object[] args) {}

    protected void onTurnResults(Object[] args) {}

    protected void onShowTime(Object[] args) {}

    protected void onFinishGame(Object[] args) {}

    protected void onDisconnect(Object[] args) {
        runOnUiThread(() -> {
            Dialogs.showInfoDialog(this, "Lost connection to server");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }

    static class Player {
        private final String id;
        private final String username;
        private final int number;

        // Game specific attributes
        private String symbol;
        private int points;

        public Player (String id, String username, int number) {
            this.id = id;
            this.username = username;
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

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public int getNumber() {
            return this.number;
        }

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }
    }
}
