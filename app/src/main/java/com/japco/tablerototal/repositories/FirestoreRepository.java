package com.japco.tablerototal.repositories;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.japco.tablerototal.model.AuthUser;

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

    public interface OnFirestoreTaskComplete<TModel> {
        void onSuccess(TModel result);
        void onError(Exception e);
    }
}
