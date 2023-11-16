package com.japco.tablerototal;

public class Constants {
    public static final String[] GAMES = new String[] { "rock_paper_scissors", "tic_tac_toe", "even_odd" };
    public static final String USERNAME_EXTRA = "username";
    public static final String SERVER_URL = BuildConfig.SERVER_URL;
    public static final String STATUS_OK = "ok";
    public static final String STATUS_ERROR = "error";
    public static final String RESPONSE_STATUS = "status";
    public static final class GameOptions {
        public static final String GAME_OPTIONS = "gameOptions";
        public static final String GAME = "game";
        public static final String PLAYERS = "maxPlayers";
        public static final String ROUNDS = "rounds";
        public static final String TIMEOUT = "timeout";
    }

    public static final class ClientEvents {
        public static final String JOIN_GAME = "join";
        public static final String CREATE_GAME = "create";
    }

    public static final class ServerEvents {
        /* v.g. game_start o game_end */
    }
}
