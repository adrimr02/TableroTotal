package com.japco.tablerototal.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.type.DateTime;
import com.japco.tablerototal.Constants;
import com.japco.tablerototal.R;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Match implements Parcelable {

    public static int getImg(String game) {
        if (game.equals(Constants.GAMES[0])) {
            return R.drawable.piedrapapeltijera;
        } else if (game.equals(Constants.GAMES[1])) {
            return R.drawable.tresenraya;
        } else if (game.equals(Constants.GAMES[2])) {
            return R.drawable.paresnones;
        } else {
            throw new IllegalArgumentException("Invalid game");
        }
    }

    private Date date;
    private String game;
    private Player[] players;

    public Match(Date date, String game, Player[] players) {
        this.date = date;
        this.game = game;
        this.players = players;
    }

    public Match() {}

    protected Match(Parcel in) {
        this.game = in.readString();
        this.date = (Date) in.readSerializable();
        this.players = (Player[]) in.readParcelableArray(Player.class.getClassLoader());
    }

    public Date getDate() {
        return date;
    }

    public String getGame() {
        return game;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setGame(String game) {
        this.game = game;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(game);
        dest.writeSerializable(date);
        dest.writeParcelableArray(players, flags);
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

    public static class Player implements Parcelable {

        public static final Creator<Player> CREATOR = new Creator<Player>() {
            @Override
            public Player createFromParcel(Parcel in) {
                return new Player(in);
            }

            @Override
            public Player[] newArray(int size) {
                return new Player[size];
            }
        };

        public String username;
        public String id;
        public int points;

        public Player(String username, String id, int points) {
            this.username = username;
            this.id = id;
            this.points = points;
        }

        public Player(Parcel in) {
            this.id = in.readString();
            this.username = in.readString();
            this.points = in.readInt();
        }

        @NonNull
        @Override
        public String toString() {
            return "Player{" +
                    "username='" + username + '\'' +
                    ", id='" + id + '\'' +
                    ", points=" + points +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel parcel, int i) {
            parcel.writeString(id);
            parcel.writeString(username);
            parcel.writeInt(points);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Match{" +
                "date='" + date + '\'' +
                ", game='" + game + '\'' +
                ", players=" + Arrays.toString(players) +
                '}';
    }
}
