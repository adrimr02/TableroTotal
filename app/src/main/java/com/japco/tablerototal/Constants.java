package com.japco.tablerototal;

public class Constants {
    public static final String[] GAMES = new String[] { "rock_paper_scissors", "tic_tac_toe", "even_odd" };
    public static final String USERNAME_EXTRA = "username";
    //public static final String SERVER_URL = BuildConfig.SERVER_URL;
    public static final String SERVER_URL = "https://wws0z5kh-3000.uks1.devtunnels.ms/L";
    public static final String STATUS_OK = "ok";
    public static final String STATUS_ERROR = "error";
    public static final String NOT_ENOUGHT_PLAYERS = "not_enough_players";
    public static final String RESPONSE_STATUS = "status";
    public static final String READY = "ready";
    public static final String NOT_READY = "not_ready";
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
        public static final String MARK_AS_READY = "mark_as_ready";
        public static final String MOVE = "move";

    }

    public static final class ServerEvents {
        // Common
        public static final String SHOW_TIME = "show_time";

        // Waiting room
        public static final String SHOW_PLAYERS_WAITING = "show_players_waiting";
        public static final String START_GAME = "start_game";

        // Game
        public static final String NEXT_TURN = "next_turn";
        public static final String SHOW_TURN_RESULTS = "show_turn_results";
        public static final String FINISH_GAME = "finish_game";

    }
}
