package com.japco.tablerototal.ui.games;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.japco.tablerototal.Constants;
import com.japco.tablerototal.R;
import com.japco.tablerototal.ui.MainActivity;
import com.japco.tablerototal.util.Dialogs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EvensAndNonesActivity extends AbstractGameActivity {

    Button btPares;
    Button btNones;
    Button btSalir;
    TextView txNumero;
    TextView txTiempo;
    TextView txPuntos;
    TextView txRonda;
    TextView[] ranking;
    TextView txRanking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paresynones);

        btNones = findViewById(R.id.botonNones);
        btPares =  findViewById(R.id.botonPares);
        btSalir = findViewById(R.id.btSalir);
        txNumero = findViewById(R.id.txNumero);
        txRonda = findViewById(R.id.txNumeroRondaOE);
        txTiempo = findViewById(R.id.txTiempoRonda);
        txPuntos = findViewById(R.id.txPuntos);
        ranking = new TextView[]{
                findViewById(R.id.txTop1),
                findViewById(R.id.txTop2),
                findViewById(R.id.txTop3)
        };
        txRanking = findViewById(R.id.txRanking);

        btSalir.setOnClickListener(v -> moveToMainActivity());

        //Enviamos la repuesta del jugador, su identificación la posee el servidor
        btNones.setOnClickListener(v -> {
            if(!txNumero.getText().toString().equals("")) {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("numberType", "nones");
                    obj.put("number", Integer.parseInt(txNumero.getText().toString()));
                    socketService.getSocket().emit(Constants.ClientEvents.MOVE, obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Desactivamos los botones para evitar más respuestas
                btPares.setEnabled(false);
                btNones.setEnabled(false);
                txNumero.setEnabled(false);
            }
        });

        btPares.setOnClickListener(v -> {
            if(!txNumero.getText().toString().equals("")){
                try {
                    JSONObject obj  = new JSONObject();
                    obj.put("numberType", "evens");
                    obj.put("number", Integer.parseInt(txNumero.getText().toString()));
                    socketService.getSocket().emit(Constants.ClientEvents.MOVE, obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Desactivamos los botones para evitar más respuestas
                btPares.setEnabled(false);
                btNones.setEnabled(false);
                txNumero.setEnabled(false);
            }
        });
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(EvensAndNonesActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onShowInitialInfo(Object[] args) {
        try {
            int round = ((JSONObject) args[0]).getInt(Constants.Keys.ROUND);
            runOnUiThread(() -> txRonda.setText(String.valueOf(round)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onTurnResults(Object[] args) {
        int points = 0;

        try {
            JSONObject info = (JSONObject) args[0];

            int round = info.getInt(Constants.Keys.ROUND);
            JSONArray chart = info.getJSONArray(Constants.Keys.CHART);

            for(int i = 0; i < chart.length(); i++){
                JSONObject obj = chart.getJSONObject(i);
                String id = obj.getString(Constants.Keys.PLAYER_ID);
                if(id.equals(socketId)){
                    points = obj.getInt(Constants.Keys.POINTS);
                    break;
                }
            }

            int finalPoints = points;

            for(int i = 0; i < chart.length(); i++) {
                if (i == 3) {
                    break;
                }

                JSONObject obj = chart.getJSONObject(i);
                String topUsername = obj.getString(Constants.Keys.USERNAME);
                int topPoints = obj.getInt(Constants.Keys.POINTS);

                String message = (i + 1) + ". " + topUsername + " (" + topPoints + "pts)";
                TextView tx = ranking[i];
                runOnUiThread(() -> {
                    tx.setText(message);
                    tx.setVisibility(View.VISIBLE);
                });
            }

            runOnUiThread(() -> {
                txPuntos.setText(finalPoints + "pts");
                txRonda.setText(String.valueOf(round));
                btNones.setEnabled(true);
                btPares.setEnabled(true);
                txNumero.setEnabled(true);
                txNumero.setText("");

                if(chart.length() > 0){
                    txRanking.setVisibility(View.VISIBLE);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onShowTime(Object[] args) {
        try {
            int counter = ((JSONObject) args[0]).getInt(Constants.Keys.COUNTER);
            runOnUiThread(() -> txTiempo.setText(counter + "s"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onFinishGame(Object[] args) {
        try {
            List<String> winners = new ArrayList<String>();
            String message = "";
            JSONObject obj = (JSONObject) args[0];
            JSONArray winner = obj.getJSONArray(Constants.Keys.WINNER);
            for(int i = 0; i < winner.length(); i++){
                String name = winner.getString(i);
                winners.add(name);
            }

            int winnerPoints = obj.getInt(Constants.Keys.POINTS);

            if(winners.size() > 1){
                message = getString(R.string.exclamation);
                for(int i = 0; i < winners.size(); i++){
                    message += winner.get(i);
                    if(i < winners.size()-2){
                        message+=", ";
                    } else if(i == winners.size()-2) {
                        message+=" " + getString(R.string.Y) + " ";
                    }
                }
                message+=" " + getString(R.string.tie) + " " + winnerPoints + " " + getString(R.string.match_points) + "!";
            } else {
                message = getString(R.string.exclamation) + winners.get(0) + " " + getString(R.string.has_won_the_match) + " "
                        + winnerPoints + " " + getString(R.string.match_points) + "!";
            }

            String finalMessage = message;
            runOnUiThread(() -> Dialogs.showInfoDialog(this, finalMessage, (dialog, which) -> {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
