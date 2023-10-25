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

import com.japco.tablerototal.util.Connection;
import com.japco.tablerototal.util.Dialogs;

public class HomeFragment extends Fragment {

    private EditText editUsername;
    private Button createGameButton;
    private Button joinGameButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        editUsername = view.findViewById(R.id.homeNameField);
        createGameButton = view.findViewById(R.id.buttonCreateGame);
        joinGameButton = view.findViewById(R.id.buttonJoinGame);

        createGameButton.setOnClickListener(v -> {
            if (!validate()) return;
            Log.i("Navigation","Redirecting to create game.");
            Intent intent = new Intent(v.getContext(), NewGameActivity.class);
            intent.putExtra("username", editUsername.getText().toString().trim());
            startActivity(intent);
        });

        joinGameButton.setOnClickListener(v -> {
            if (!validate()) return;
            Log.i("Navigation","Redirecting to join game.");
            Intent intent = new Intent(v.getContext(), JoinGameActivity.class);
            intent.putExtra(Constants.USERNAME_EXTRA, editUsername.getText().toString().trim());
            startActivity(intent);
        });

    }

    private boolean validate() {
        boolean hasConnection = new Connection(getActivity()).checkConnection();
        if (!hasConnection) {
            Dialogs.showInfoDialog(getActivity(), R.string.no_connection_dialog_message);
            return false;
        }

        if (editUsername.getText().toString().trim().isEmpty()) {
            Dialogs.showInfoDialog(getActivity(), R.string.no_nickname_dialog_message);
            return false;
        }
        return true;
    }
}