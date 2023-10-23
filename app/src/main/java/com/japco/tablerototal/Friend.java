package com.japco.tablerototal;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Friend implements Parcelable {

    private String name;
    private String lastConnection;

    public Friend(String name, String lastConnection) {
        this.name = name;
        this.lastConnection = lastConnection;
    }

    protected Friend(Parcel in) {
        name = in.readString();
        lastConnection = in.readString();
    }

    public static final Creator<Friend> CREATOR = new Creator<Friend>() {
        @Override
        public Friend createFromParcel(Parcel in) {
            return new Friend(in);
        }

        @Override
        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastConnection() {
        return lastConnection;
    }

    public void setLastConnection(String lastConnection) {
        this.lastConnection = lastConnection;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(lastConnection);
    }
}
