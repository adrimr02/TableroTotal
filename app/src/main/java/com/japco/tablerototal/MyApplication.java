package com.japco.tablerototal;

import android.app.Application;

import com.japco.tablerototal.model.AuthUser;

public class MyApplication extends Application {
    private AuthUser user;


    public AuthUser getUser() {
        return user;
    }

    public void setUser(AuthUser user) {
        this.user = user;
    }
}
