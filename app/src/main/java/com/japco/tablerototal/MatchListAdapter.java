package com.japco.tablerototal;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.japco.tablerototal.model.Match;

import java.util.List;

public class MatchListAdapter extends RecyclerView.Adapter<MatchListAdapter.GameMatchViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Match item);
    }
    private List<Match> matchsList;
    private final OnItemClickListener listener;

    public MatchListAdapter(List<Match> matchsList, OnItemClickListener listener) {
        this.matchsList = matchsList;
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
        Match game = matchsList.get(position);
        Log.i("Lista","Visualiza elemento: "+ game);
        holder.bindUser(game, listener);
    }

    @Override
    public int getItemCount() {
        return matchsList.size();
    }

    public static class GameMatchViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView date;
        private TextView winner;
        private ImageView gameLogo;
        public GameMatchViewHolder(View itemView) {
            super(itemView);
            title= (TextView)itemView.findViewById(R.id.gametitle);
            date= (TextView)itemView.findViewById(R.id.date);
            winner= (TextView)itemView.findViewById(R.id.winner);
            gameLogo = (ImageView)itemView.findViewById(R.id.gameLogo);
        }

        public void bindUser(final Match match, final OnItemClickListener listener) {
            title.setText(match.getGame());
            date.setText(match.getDate());
            winner.setText("Ganador: " + match.getWinner());
            gameLogo.setImageResource(match.getImgDirectory());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Log.i("Hola", "Hola");
                    listener.onItemClick(match);
                }
            });
        }
    }
}
