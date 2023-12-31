package com.japco.tablerototal.ui.games;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;

import com.japco.tablerototal.Constants;
import com.japco.tablerototal.util.SocketService;

public abstract class AbstractGameActivity extends AppCompatActivity {

    protected SocketService socketService;

    protected String userId = null;

    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            SocketService.SocketBinder binder = (SocketService.SocketBinder) service;
            socketService = binder.getService();
            userId = socketService.getSocket().id();
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

    abstract void addSocketListeners();
    abstract void removeSocketListeners();

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
