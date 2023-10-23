package com.japco.tablerototal;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Match implements Parcelable {

    private String date;
    private String game;
    private String winner;

    private int imgDirectory;

    public Match(String date, String game, String winner, int imgDirectory) {
        this.date = date;
        this.game = game;
        this.winner = winner;
        this.imgDirectory = imgDirectory;
    }
    protected Match(Parcel in) {
        this.date = in.readString();
        this.game = in.readString();
        this.winner = in.readString();
    }

    public String getDate() {
        return date;
    }

    public String getGame() {
        return game;
    }

    public String getWinner() {
        return winner;
    }

    public int getImgDirectory() {
        return imgDirectory;
    }
    public void setImgDirectory(int imgDirectory) {
        this.imgDirectory = imgDirectory;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(game);
        dest.writeString(winner);
    }

    public static final Creator<Match> CREATOR = new Creator<Match>() {
        @Override
        public Match createFromParcel(Parcel in) {
            return new Match(in);
        }

        @Override
        public Match[] newArray(int size) {
            return new Match[size];
        }
    };


}
