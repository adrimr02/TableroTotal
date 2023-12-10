package com.japco.tablerototal;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.japco.tablerototal.util.Connection;
import com.japco.tablerototal.util.Dialogs;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private EditText editUsername;
    private Button createGameButton;
    private Button joinGameButton;
    private Button btnLogout;

    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance();
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        editUsername = view.findViewById(R.id.homeNameField);
        createGameButton = view.findViewById(R.id.buttonCreateGame);
        joinGameButton = view.findViewById(R.id.buttonJoinGame);

        btnLogout = view.findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            logout();
        });

        createGameButton.setOnClickListener(v -> {
            if (!isValid()) return;
            Log.i("Navigation","Redirecting to create game.");
            Intent intent = new Intent(v.getContext(), NewGameActivity.class);
            intent.putExtra("username", editUsername.getText().toString().trim());
            startActivity(intent);
        });

        joinGameButton.setOnClickListener(v -> {
            if (!isValid()) return;
            Log.i("Navigation","Redirecting to join game.");
            Intent intent = new Intent(v.getContext(), JoinGameActivity.class);
            intent.putExtra(Constants.USERNAME_EXTRA, editUsername.getText().toString().trim());
            startActivity(intent);
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String username = auth.getCurrentUser().getDisplayName();
            if (username != null) {
                editUsername.setText(username);
            }
        } else {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    private boolean isValid() {
        boolean hasConnection = new Connection(getActivity()).checkConnection();
        if (!hasConnection) {
            Dialogs.showInfoDialog(getActivity(), R.string.no_connection_dialog_message);
            return false;
        }

        ((MyApplication) requireActivity().getApplication()).setUsername(editUsername.getText().toString().trim());
        return true;
    }

    private void logout() {
        Dialogs.showConfirmDialog(getContext(), R.string.logout_text, (d,i) -> {
            d.dismiss();
            auth.signOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        });
    }
}