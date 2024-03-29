package com.japco.tablerototal.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.japco.tablerototal.R;
import com.japco.tablerototal.model.Match;
import java.util.Collections;
import java.util.List;

public class MatchListAdapter extends RecyclerView.Adapter<MatchListAdapter.GameMatchViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Match item);
    }
    private final List<Match> matchList;
    private final OnItemClickListener listener;

    public MatchListAdapter(List<Match> matchList, OnItemClickListener listener) {
        this.matchList = matchList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GameMatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.line_recycler_view_record, parent, false);
        return new GameMatchViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull GameMatchViewHolder holder, int position) {
        Match game = matchList.get(position);
        Log.i("Lista","Visualiza elemento: "+ game);
        holder.bindUser(game, listener);
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    public static class GameMatchViewHolder extends RecyclerView.ViewHolder{
        private final TextView title;
        private final TextView date;
        private final TextView winner;
        private final ImageView gameLogo;

        public GameMatchViewHolder(View itemView) {
            super(itemView);
            title= itemView.findViewById(R.id.gametitle);
            date= itemView.findViewById(R.id.date);
            winner= itemView.findViewById(R.id.winner);
            gameLogo = itemView.findViewById(R.id.gameLogo);
        }

        public void bindUser(final Match match, final OnItemClickListener listener) {
            Context context = winner.getContext();

            title.setText(context.getString(Match.getGame(match.getGame())));
            date.setText(match.getDate());
            gameLogo.setImageResource(Match.getImg(match.getGame()));
            winner.setText(winnersToString(match.getWinners(), context));

            itemView.setOnClickListener(v -> {
                Log.i("Partida", "Partida pulsada");
                listener.onItemClick(match);
            });
        }

        private String winnersToString(List<Match.Player> winners, Context context){
            String and = context.getString(R.string.Y);
            String result = "";

            if(winners.size() > 1){
                result = context.getString(R.string.Empate) + " ";
            }

            for(int i = 0; i < winners.size(); i++){
                result += winners.get(i).username;

                if(i < winners.size()-2){
                    result += ", ";
                } else if(i == winners.size()-2){
                    result += " " + and + " ";
                }

            }

            return result;
        }
    }
}
