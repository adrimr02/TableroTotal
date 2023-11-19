package com.japco.tablerototal.util;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.japco.tablerototal.Constants;

import java.net.URI;
import java.net.URISyntaxException;

import io.socket.client.Manager;
import io.socket.client.Socket;

public class SocketService extends Service {

    private final IBinder socketBinder = new SocketBinder();
    private Socket mSocket;

    public Socket getSocket() {
        return mSocket;
    }

    @Override
    public void onCreate() {
        try {
            Manager manager = new Manager(new URI(Constants.SERVER_URL));
            mSocket = manager.socket("/play");
            mSocket.connect();
        } catch (URISyntaxException ignored) {}
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return socketBinder;
    }

    @Override
    public void onDestroy() {
        mSocket.close();
        super.onDestroy();
    }

    public class SocketBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
        }
    }
}
