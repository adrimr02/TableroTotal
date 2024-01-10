package com.japco.tablerototal.repositories;

import com.google.common.collect.Lists;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.japco.tablerototal.model.AuthUser;
import com.japco.tablerototal.model.Match;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FirestoreRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getUser(String userId, OnFirestoreTaskComplete<DocumentSnapshot> onComplete) {
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                onComplete.onSuccess(task.getResult());
            } else {
                onComplete.onError(task.getException());
            }
        });
    }

    public void setUser(AuthUser user, OnFirestoreTaskComplete<Void> onComplete) {
        db.collection("users").document(user.getUserId()).set(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                onComplete.onSuccess(null);
            } else {
                onComplete.onError(task.getException());
            }
        });
    }

    public void getHistory(String userId, OnFirestoreTaskComplete<List<Match>> onComplete) {
        db.collection("users").document(userId)
                .collection("games").limit(20).orderBy("date", Query.Direction.ASCENDING).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Match> records = new ArrayList<>();
                task.getResult().forEach(record -> {
                    Match match = new Match();
                    match.setGame(record.getString("game"));
                    match.setDate(record.getDate("date"));
                    List<Map<String, Object>> recordPlayers = (List<Map<String, Object>>) record.get("players");
                    List<Match.Player> players = new ArrayList<>();
                    assert recordPlayers != null;
                    for (Map<String, Object> player : recordPlayers) {
                        players.add(new Match.Player((String) player.get("username"),
                                (String) player.get("authId"), Math.toIntExact((Long) player.get("points"))));
                    }

                    match.setPlayers(players.toArray(new Match.Player[0]));
                    records.add(match);

                });
                onComplete.onSuccess(records);
            } else {
                onComplete.onError(task.getException());
            }
        });
    }

    public interface OnFirestoreTaskComplete<TModel> {
        void onSuccess(TModel result);
        void onError(Exception e);
    }
}
