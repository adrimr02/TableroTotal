package com.japco.tablerototal;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.japco.tablerototal.model.AuthUser;
import com.japco.tablerototal.util.Dialogs;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    static final int RC_SIGN_IN = 40;

    private FirebaseAuth auth;
    private FirebaseFirestore database;

    private GoogleSignInClient mGoogleSignInClient;
    Dialog usernameDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        System.out.println("Login activity");
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();;

        ConstraintLayout signIn = findViewById(R.id.signInButton);

        usernameDialog = new Dialog(LoginActivity.this);

        // progressDialog = new ProgressDialog(this);
        // progressDialog.setTitle("Signin In");
        // progressDialog.setMessage("we are creating your account");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signIn.setOnClickListener(v -> {
            signIn();
        });
    }

    private void signIn() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuth(account.getIdToken());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       FirebaseUser fUser = auth.getCurrentUser();
                       if (fUser != null) {
                           boolean newUser = Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser();
                           AuthUser user = new AuthUser();
                           user.setUserId(fUser.getUid());
                           user.setName(fUser.getDisplayName());
                           user.setProfile(fUser.getPhotoUrl() != null ? fUser.getPhotoUrl().toString() : null);
                           if (newUser) {
                               usernameDialog.setContentView(R.layout.dialog_welcome);
                               usernameDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                               usernameDialog.setCancelable(false);
                               usernameDialog.getWindow().getAttributes().windowAnimations = R.style.animation;

                               EditText editUsername = usernameDialog.findViewById(R.id.editTextUsername);
                               Button btnSaveUsername = usernameDialog.findViewById(R.id.btnSaveUsername);

                               btnSaveUsername.setOnClickListener(v -> {
                                   String username = String.valueOf(editUsername.getText());
                                   if (username.trim().isEmpty()) {
                                       Dialogs.showInfoDialog(usernameDialog.getContext(), R.string.no_nickname_dialog_message);
                                   } else {
                                       user.setUsername(username);
                                       usernameDialog.dismiss();
                                   }
                               });

                               usernameDialog.show();
                           } else {
                               String username = database.collection("users").document(user.getUserId()).get().getResult().getString("username");
                               user.setUsername(username);
                           }


                           database.collection("users").document(user.getUserId()).set(user)
                                   .addOnSuccessListener(aVoid -> {
                                       Intent intent = new Intent(this, MainActivity.class);
                                       startActivity(intent);
                                   })
                                   .addOnFailureListener(e -> {
                                       Toast.makeText(this, "Error while signin in. Please try again", Toast.LENGTH_SHORT).show();
                                   });
                       }
                   } else {
                       Toast.makeText(this, "Error while signin in. Please try again", Toast.LENGTH_SHORT).show();
                   }
                });
    }
}
